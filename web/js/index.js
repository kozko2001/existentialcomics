import React from 'react';
import ReactDOM from 'react-dom';
import App from './containers/App';
import 'whatwg-fetch';

ReactDOM.render(<App />, document.getElementById('main'));

fetch('http://comic.allocsoc.net/comics')
    .then(function(response) {
	return response.json();
    }).then(function(json) {
	console.log(json);
    }).catch(function(err) {
	console.log(err);
    });

