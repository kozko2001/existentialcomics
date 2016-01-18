FROM ubuntu
MAINTAINER Jordi Coscolla

RUN apt-get update
RUN apt-get install -y python python-pip python-dev libxml2-dev libxslt-dev \
                      libffi-dev libssl-dev libjpeg-dev supervisor

ADD . /srv/project
COPY dockerfiles/crontab /var/spool/cron/crontabs/root
COPY dockerfiles/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

WORKDIR /srv/project

RUN pip install -r scrape/requirements.txt

CMD ["/usr/bin/supervisord"]
