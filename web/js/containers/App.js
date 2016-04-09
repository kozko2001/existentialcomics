import React from 'react';
import {Provider} from 'react-redux';
import configureStore from '../store/configureStore';
import ListComics from '../components/ListComics';
import {renderDevTools} from '../utils/devTools';

const store = configureStore();

export default class extends React.Component {
  constructor(props) {
    super(props);
  }
  render() {
    return (
      <div>

        {/* <Home /> is your app entry point */}
        <Provider store={store}>
          <ListComics />
        </Provider>

        {/* only renders when running in DEV mode */
          renderDevTools(store)
        }
      </div>
    );
  }
};
