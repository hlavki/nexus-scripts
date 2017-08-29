# groovy script to list or delete assets based on regular expression

## Deploy scripts

```bash
provision.sh -h https://repository.host.com -u admin -p ****
```

## Call script


### List assets

```bash
curl -v -X POST -u admin:****** --header "Content-Type: text/plain" -d "{\"repoName\": \"raw-static-content\", \"assetName\": \"^prefix/.*\"}" "https://repository.host.com/service/siesta/rest/v1/script/listAssets/run
```

### Delete assets

```bash
curl -v -X POST -u admin:****** --header "Content-Type: text/plain" -d "{\"repoName\": \"raw-static-content\", \"assetName\": \"^prefix/.*\"}" "https://repository.host.com/service/siesta/rest/v1/script/deleteAssets/run
```
