FROM ubuntu
MAINTAINER Jordi Coscolla

RUN apt-get update
RUN apt-get install -y python python-pip python-dev libxml2-dev libxslt-dev libffi-dev libssl-dev  libjpeg-dev
#RUN pip install lxml && pip install pyopenssl && pip install scrapy && pip install pymongo

ADD . /srv/project
WORKDIR /srv/project
RUN pip install -r scrape/requirements.txt

CMD ["/bin/bash"]
