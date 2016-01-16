# exitentialcomics

if not docker-machine is created

```
docker-machine create dev
`docker-machine env dev`
```

```
docker build -t comicstrip .

docker run --name mongodb -d mongo

docker run -ti -v `pwd`:/srv/project --link mongodb:mongo comicstrip bash 
```

