import 'isomorphic-fetch';
import {API_ENDPOINT} from '../constants/Constants';

function addTopicsSubscribed(topic) {
  let topics = getTopicsSubscribed();
  let alreadySubscribed = topics.indexOf(topic) !== -1
  
  if(!alreadySubscribed) {
    topics.push(topic);

    localStorage.setItem('topics', JSON.stringify(topics));
    console.log('addTopics... ', topics)
  }
  return topics;
}

function removeTopicsSubscribed(topic) {
  let topics = getTopicsSubscribed().filter((t) => t !== topic);
  localStorage.setItem('topics', JSON.stringify(topics));

  return topics
}

function registerPush(topics) {
  return navigator.serviceWorker.register('service-worker.js')
    .then(function(registration) {
      return registration.pushManager.getSubscription()
	.then(function(subscription) {
	  
	  if (subscription) {
	    return subscription;
	  }
	  
	  return registration.pushManager.subscribe({ userVisibleOnly: true });
	});
    }).then(function(subscription) {
      console.log(subscription);
      
      let endpoint = subscription.endpoint;

      localStorage.setItem('token', endpoint);
      
      return fetch(`${API_ENDPOINT}webpush/register`, {
	method: 'POST',
	headers: {
	  'Accept': 'application/json',
	  'Content-Type': 'application/json'
	},
	body: JSON.stringify({
	  topics,
	  endpoint
	})
      });
    });
}

export function subscribe(comic_id) {
  let topics = addTopicsSubscribed(comic_id);
  
  return registerPush(topics);
}

export function unsubscribe(comic_id) {
  let topics = removeTopicsSubscribed(comic_id);

  return registerPush(topics);
}

export function isSubscribed(comic_id) {
  getTopicsSubscribed().filter((t) => t === comic_id).length > 0;
}

export function getTopicsSubscribed() {
  let cached = localStorage.getItem('topics');

  if(cached) {
    return JSON.parse(cached)
  } else {
    return []
  }
}
