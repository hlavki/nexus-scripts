import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet
import org.sonatype.nexus.repository.raw.internal.RawFormat

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

// https://gist.github.com/kellyrob99/2d1483828c5de0e41732327ded3ab224
// https://gist.github.com/emexelem/bcf6b504d81ea9019ad4ab2369006e66

def request = new JsonSlurper().parseText(args)
assert request.repoName: 'repoName parameter is required'
assert request.assetName: 'name regular expression parameter is required, format: regexp'

log.info("Gathering Asset list for repository: ${request.repoName} as of pattern: ${request.assetName}")

def repo = repository.repositoryManager.get(request.repoName)

assert repo: "Repository ${request.repoName} does not exist"
//assert repo.format instanceof RawFormat: "Repository ${request.repoName} is not raw, but ${repo.format}"

StorageFacet storageFacet = repo.facet(StorageFacet)
def tx = storageFacet.txSupplier().get()

try {
    tx.begin()

    Iterable<Asset> assets = tx.
        findAssets(Query.builder().where('name MATCHES ').param(request.assetName).build(), [repo])

    def urls = assets.collect { "/repository/${repo.name}/${it.name()}" }

    tx.commit()

    def result = JsonOutput.toJson([
        assets    : urls,
        assetName : request.assetName,
        repoName  : request.repoName
    ])
    return result

} catch (Exception e) {
    log.warn("Error occurs while deleting snapshot images from docker repository: {}", e.toString())
    tx.rollback()
} finally {
    // @todo Fix me! Danger Will Robinson!  
    tx.close()
}
