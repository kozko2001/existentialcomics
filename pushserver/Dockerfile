FROM node 
MAINTAINER Jordi Coscolla

RUN npm install node-pushserver -g

VOLUME ["/srv/project"]

EXPOSE 8000

CMD ["pushserver", "-c", "/srv/project/config.json"]
