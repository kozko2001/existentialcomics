import {expect} from 'chai';
import fetchMock from 'fetch-mock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import {fetchStrips} from '../js/actions/ListStripActions';
import * as types from '../js/constants/ActionTypes';
import reducer from '../js/reducers/ListStripReducer';

const middlewares = [ thunk ];
const mockStore = configureMockStore(middlewares);

const stripTest = {
  '_id': '570bb2967e6ecc4ddfd1272b', 
  'comic': 'xkcd', 
  'order': 1666, 
  'text': 'I just spent 20 minutes deciding whether to start an email with "Hi" or "Hey", so I think it transferred correctly.', 
  'title': 'Brain Upload', 
  'url': 'http://xkcd.com/1666/'
}

describe('strips list', () => {
  describe('actions', () => {
    afterEach(() => {
      fetchMock.restore();
    })

    it('fetchStrips actions with good connectivity', (done) => {
      fetchMock.mock('http://comic.allocsoc.net/comics/xkcd', {result: [stripTest]});

      const store = mockStore({loading: false, data: {}})

      store.dispatch(fetchStrips('xkcd'))
	.then(() => {
	  let actions = store.getActions()
	  expect(actions).to.have.deep.property('[0].type', types.STRIP_ON_LODING);
	  expect(actions).to.have.deep.property('[1].type', types.STRIP_SUCCEDED);
	  expect(actions).to.have.deep.property('[1].data[0].title', 'Brain Upload');
	})
	.then(done)
	.catch(done);
    })
    it('fetchStrips action with error in the network', (done) => {
      fetchMock.mock('http://comic.allocsoc.net/comics/xkcd', {throws: 'connectivity error'});
      const store = mockStore({loading: false, data: []})

      store.dispatch(fetchStrips('xkcd'))
	.then(() => {
	  let actions = store.getActions()
	  expect(actions).to.have.deep.property('[0].type', types.STRIP_ON_LODING);
	  expect(actions).to.have.deep.property('[1].type', types.STRIP_FAILED);
	})
	.then(done)
	.catch(done);
    })
  });

  describe('reducers', () => {
    it('initial state', () => {
      const initialState = reducer(undefined, {});
      const expectedState = {loading: false, data: {}, last_error: undefined }
      
      
      expect(initialState).to.deep.equal(expectedState);
    });
    it('state after start loading the data', () => {
      const initialState = {loading: false, data: {}, last_error: 'error'};
      const expectedState = {loading: true, data: {}, last_error: 'error' }
      const state = reducer(initialState, {type: types.STRIP_ON_LODING});
      
      expect(state).to.deep.equal(expectedState);
    })
    it('data fetched...', () => {
      const initialState = {loading: true, data: {}, last_error: 'error'};
      const expectedState = {
	loading: false,
	data: {
	  'xkcd': [stripTest]
	},
	last_error: undefined
      };

      const state = reducer(initialState, {
	type: types.STRIP_SUCCEDED,
	data: [stripTest],
	comic_id: 'xkcd'
      });
      expect(state).to.deep.equal(expectedState);
    })
  });
});
