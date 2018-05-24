import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Component
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

// https://gist.github.com/kellyrob99/2d1483828c5de0e41732327ded3ab224
// https://gist.github.com/emexelem/bcf6b504d81ea9019ad4ab2369006e66

def request = new JsonSlurper().parseText(args)
assert request.repoName: 'repoName parameter is required'

log.info("Gathering Asset list for repository: ${request.repoName} as of pattern: ${request.path}")

def repo = repository.repositoryManager.get(request.repoName)

assert repo: "Repository ${request.repoName} does not exist"

StorageFacet storageFacet = repo.facet(StorageFacet)
def tx = storageFacet.txSupplier().get()

tx.begin()

Query.Builder qb = Query.builder()
if (request.group) {
    qb = qb.where(' group MATCHES ').param(request.group);
}
if (request.name) {
    qb = qb.hasWhere() ? qb.and(' name MATCHES ').param(request.name) : qb.where(' name MATCHES ').param(request.name)
}

Iterable<Component> components = tx.findComponents(qb.build(), [repo])

def cmps = components.collect {
    [
        group: it.group(),
        name: it.name(),
        version: it.version()
    ]
}

components.each { cmp ->
    log.info("Deleting component ${cmp.name()}")
    tx.deleteComponent(cmp)
}

tx.commit()

def result = JsonOutput.toJson([
        components : cmps,
        group      : request.group,
        name       : request.name,
        repoName   : request.repoName
    ])
return result
