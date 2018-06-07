import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Component
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

// https://gist.github.com/kellyrob99/2d1483828c5de0e41732327ded3ab224
// https://gist.github.com/emexelem/bcf6b504d81ea9019ad4ab2369006e66

final SNAPSHOT_SUFFIX = "-SNAPSHOT"

def request = new JsonSlurper().parseText(args)
assert request.repoName: 'repoName parameter is required'

log.info("Gathering Asset list for repository: ${request.repoName} as of pattern: ${request.path}")

def repo = repository.repositoryManager.get(request.repoName)
assert repo: "Repository ${request.repoName} does not exist"

StorageFacet storageFacet = repo.facet(StorageFacet)
def tx = storageFacet.txSupplier().get()

try {
    tx.begin()

    Iterable<Component> components = tx.findComponents(Query.builder().build(), [repo])

    def snapshots = components.findAll{ it.version().contains(SNAPSHOT_SUFFIX)}.collectEntries {[(it.name() + ":" + it.version()), it]}
    def releases = components.findAll{ !it.version().contains(SNAPSHOT_SUFFIX)}.collect {it.name() + ":" + it.version()}.toSet()

    def toRemove = snapshots.findAll{ k,v -> releases.contains(k - SNAPSHOT_SUFFIX) }.collect{ k, v -> v }

    toRemove.each { cmp ->
        log.info("Deleting component ${cmp.name()}:${cmp.version()}")
        tx.deleteComponent(cmp)
    }

    def removed = toRemove.collect {
        [
            group: it.group(),
            name: it.name(),
            version: it.version()
        ]
    }

    tx.commit()

    def result = JsonOutput.toJson([
        removedComponents : removed,
        repoName   : request.repoName
    ])
    return result

} catch (Exception e) {
    log.warn("Error occurs while deleting snapshot images from docker repository: {}", e.toString())
    tx.rollback()
} finally {
    // @todo Fix me! Danger Will Robinson!  
    tx.close()
}
