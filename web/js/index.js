import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';
import configureStore from './store/configureStore';
import ListComics from './components/ListComics';
import ListStrips from './components/ListStrips';
import App from './containers/App';
import {renderDevTools} from './utils/devTools';
import {syncHistoryWithStore} from 'react-router-redux';
import {Router, Route, browserHistory, IndexRoute} from 'react-router';

const store = configureStore();
const history = syncHistoryWithStore(browserHistory, store);

ReactDOM.render(
  <div>
    <Provider store={store}>
      <Router history={history}>
	<Route path='/' component={App}>
	  <IndexRoute component={ListComics} />
	  <Route path='detail/:comicId' component={ListStrips} />
	</Route>
      </Router>
    </Provider>
    {renderDevTools(store)}
  </div>, document.getElementById('main'));
