# groovy script to list or delete assets based on regular expression

## Deploy scripts

```bash
provision.sh -h https://repository.host.com -u admin -p ****
```

## Remove scripts

```bash
delete.sh -h https://repository.host.com -u admin -p ****
```

## Call script

### Delete subtree in Maven repository

Let's say you need to delete all artifacts with groupId prefix `com.example.app`

first delete all components in subtree:
```bash
curl -v -X POST -u 'admin:******' \
  --header "Content-Type: text/plain" \
  -d '{"repoName": "maven-snapshots", "group": "^com\\.example\\.app.*"}' \
  https://repository.example.com/service/rest/v1/script/deleteComponents/run
```

then delete all orphaned asets like hashes, etc.
```bash
curl -v -X POST -u 'admin:******' \
    --header "Content-Type: text/plain" \
    -d '{"repoName": "maven-snapshots", "assetName": "^com/example/app/.*"}' \
    https://repository.xit.camp/service/rest/v1/script/deleteAssets/run
```

### List assets from RAW repository

```bash
curl -v -X POST -u admin:****** \
    --header "Content-Type: text/plain" \
    -d '{"repoName": "raw-static-content", "assetName": "^prefix/.*"}' \
    https://repository.host.com/service/rest/v1/script/listAssets/run
```

### List assets from RAW repository

```bash
curl -v -X POST -u admin:****** \
    --header "Content-Type: text/plain" \
    -d '{"repoName": "raw-static-content", "assetName": "^prefix/.*"}' \
    https://repository.host.com/service/rest/v1/script/deleteAssets/run
```

### Delete released snapshots from Docker

```bash
curl -v -X POST -u admin:****** \
    --header "Content-Type: text/plain" \
    -d '{"repoName": "docker"}' \
    https://repository.host.com/service/rest/v1/script/deleteDockerReleasedSnapshots/run
```
