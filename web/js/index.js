import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';
import configureStore from './store/configureStore';
import ListComics from './components/ListComics';
import ListStrips from './components/ListStrips';
import App from './containers/App';
import {syncHistoryWithStore} from 'react-router-redux';
import {Router, Route, browserHistory, hashHistory, IndexRoute} from 'react-router';
import style from '../css/index';

const store = configureStore();
const history = syncHistoryWithStore(hashHistory, store);

ReactDOM.render(
  <div>
    <Provider store={store}>
      <Router history={history}>
	<Route path='/' component={App}>
	  <IndexRoute component={ListComics} />
	  <Route path='detail/:comicId' component={ListStrips} />
	  <Route path='detail/:comicId/:stripId' component={ListStrips} />
	</Route>
      </Router>
    </Provider>
  </div>, document.getElementById('main'));
