# About

Test based on  [blog post](https://medium.com/@tejohnso/a-minimal-java-api-with-jetty-jersey-jackson-and-gradle-9842a145815e).


# To reproduce the issue Reset Connection:

./gradlew integration


## Test Sample App

To reverse `[1, 2, 3]` and `[10, 11, 12]` execute:

``` bash
curl -v -H "content-type: application/json" -d '[[1,2,3],[10,11,12]]' "https://abstract-plane-178719.appspot.com/reverse-arrays"
```

The app returns: `[[3,2,1],[12,11,10]]`.

## Build

``` bash
./gradlew build
```

## Test Locally

``` bash
./gradlew appRun
curl -v -H "content-type: application/json" -d '[[1,2,3],[10,11,12]]' "http://localhost/reverse-arrays"
```

## Unit Tests

``` bash
./gradlew test
```

## Integration Test

``` bash
./gradlew integration
```

## Deploy

``` bash
./gradlew appengineDeploy
```
