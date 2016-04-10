import React from 'react';
import {Provider} from 'react-redux';
import configureStore from '../store/configureStore';
import ListComics from '../components/ListComics';
import {renderDevTools} from '../utils/devTools';
import {syncHistoryWithStore} from 'react-router-redux';
import {Router, Route, browserHistory} from 'react-router';

const store = configureStore();
const history = syncHistoryWithStore(browserHistory, store);

export default class extends React.Component {
  constructor(props) {
    super(props);
  }
  render() {
    return (
      <div>
	<Provider store={store}>
	  <Router history={history}>
	    <Route path='/' component={ListComics}>
	      <Route path='/detail' component={ListComics} />
            </Route>
          </Router>
	</Provider>
	{  renderDevTools(store) }
      </div>
    );
    
  }
};
