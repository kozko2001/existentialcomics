import {expect} from 'chai';
import fetchMock from 'fetch-mock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import {fetchComics} from '../js/actions/ListComicActions';
import * as types from '../js/constants/ActionTypes';
import reducer from '../js/reducers/ListComicReducer';
import TestUtils from 'react-addons-test-utils';
import React from 'react';
import {Provider} from 'react-redux';
import ListComics from '../js/components/ListComics';

const middlewares = [ thunk ];
const mockStore = configureMockStore(middlewares);

const comicTest = {
  '_id': '570490516f9146050006c2f5',
  'comic_id': 'existentialcomics',
  'image': 'https://pbs.twimg.com/profile_images/546860711867973632/UJQvBiCM_200x200.jpeg',
  'name': 'Existential Comics',
  'url': 'http://existentialcomics.com/'
}

describe('comic list', () => {
  describe('actions', () => {
    afterEach(() => {
      fetchMock.restore();
    });
    
    it('fetchComics actions with good coonectivity', (done) => {
      fetchMock.mock('http://comic.allocsoc.net/comics', { comics: [comicTest] });

      const store = mockStore({loading: false, data: []})

      store.dispatch(fetchComics())
	.then(() => {
	  let actions = store.getActions()
	  expect(actions).to.have.deep.property('[0].type', types.COMIC_ON_LODING);
	  expect(actions).to.have.deep.property('[1].type', types.COMIC_SUCCEDED);
	  expect(actions).to.have.deep.property('[1].data[0].comic_id', 'existentialcomics');
	})
	.then(done)
	.catch(done);
    }),
    it('fetchComics action with error in the network', (done) => {
      fetchMock.mock('http://comic.allocsoc.net/comics', {throws: 'connectivity error'});
      const store = mockStore({loading: false, data: []})

      store.dispatch(fetchComics())
	.then(() => {
	  let actions = store.getActions()
	  expect(actions).to.have.deep.property('[0].type', types.COMIC_ON_LODING);
	  expect(actions).to.have.deep.property('[1].type', types.COMIC_FAILED);
	})
	.then(done)
	.catch(done);
    })
  }),
  describe('reducers', () => {
    it('initial state', () => {
      const initialState = reducer(undefined, {});
      const expectedState = {loading: false, data: [], last_error: undefined }
      
      expect(initialState).to.deep.equal(expectedState);
    }),
    it('state after start loading the data', () => {
      const initialState = {loading: false, data: [], last_error: 'error'};
      const expectedState = {loading: true, data: [], last_error: 'error' }
      const state = reducer(initialState, {type: types.COMIC_ON_LODING});
      
      expect(state).to.deep.equal(expectedState);
    }),
    it('data fetched...', () => {
      const initialState = {loading: true, data: [], last_error: 'error'};
      const expectedState = {loading: false,
			     data: [
			       comicTest
			     ],
			     last_error: undefined
			    };

      const state = reducer(initialState, {
	type: types.COMIC_SUCCEDED,
	data: [comicTest]
      });
      expect(state).to.deep.equal(expectedState);
    }),
    it('data error on first load', () => {
      const initialState = {loading: false, data: [], last_error: undefined};
      const expectedState = {loading: false, data: [], last_error: 'error'};
      const state = reducer(initialState, {
	type: types.COMIC_FAILED,
	err: {message: 'error'}
      });
      expect(state).to.deep.equal(expectedState);
    });
  }),
  describe('react component', () => {
    it('initial state', () => {
      const store = mockStore({listComicReducer: {loading: false, data: []}});
      let renderedComponent = TestUtils.renderIntoDocument(
	  <Provider store={store}>
	  
	  <ListComics title='hola :)' />
	  </Provider>);
      var button = TestUtils.findRenderedDOMComponentWithTag(renderedComponent, 'button');
      expect(button.type).to.be.equal('submit');
    });
  });
})
