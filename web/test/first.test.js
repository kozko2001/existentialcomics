import {expect} from 'chai'
import nock from 'nock'
import configureMockStore from 'redux-mock-store'
import thunk from 'redux-thunk'
import {fetchComics} from '../js/actions/HomeActions'
import * as types from '../js/constants/ActionTypes'
import reducer from '../js/reducers/Api'

const middlewares = [ thunk ]
const mockStore = configureMockStore(middlewares)

const comicTest = {
  "_id": "570490516f9146050006c2f5",
  "comic_id": "existentialcomics",
  "image": "https://pbs.twimg.com/profile_images/546860711867973632/UJQvBiCM_200x200.jpeg",
  "name": "Existential Comics",
  "url": "http://existentialcomics.com/"
}

describe('comic list', () => {
  describe('actions', () => {
    afterEach( () => {
      nock.cleanAll()
    }),
    it('fetchComics actions with good coonectivity', (done) => {
      nock('http://comic.allocsoc.net/')
	.get('/comics')
	.reply(200, {
	  comics: [comicTest]
	});

      const store = mockStore({loading: false, data: []})

      store.dispatch(fetchComics())
	.then(() => {
	  let actions = store.getActions()
	  expect(actions).to.have.deep.property('[0].type', types.COMIC_ON_LODING);
	  expect(actions).to.have.deep.property('[1].type', types.COMIC_SUCCEDED);
	  expect(actions).to.have.deep.property('[1].data[0].comic_id', "existentialcomics");
	})
	.then(done)
	.catch(done);
    }),
    it('fetchComics action with error in the network', (done) => {
      nock('http://comic.allocsoc.net/')
	.get('/comics')
	.replyWithError('Connectivity error');
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
      const initialState = {loading: false, data: [], last_error: "error"};
      const expectedState = {loading: true, data: [], last_error: "error" }
      const state = reducer(initialState, {type: types.COMIC_ON_LODING});
      
      expect(state).to.deep.equal(expectedState);
    }),
    it('data fetched...', () => {
      const initialState = {loading: true, data: [], last_error: "error"};
      const expectedState = {loading: false,
			     data: [
			       comicTest
			     ],
			     last_error: undefined
			    }

      const state = reducer(initialState, {
	type: types.COMIC_SUCCEDED,
	data: [comicTest]
	})
      expect(state).to.deep.equal(expectedState);
    }),
    it('data error on first load', () => {
      const initialState = {loading: false, data: [], last_error: undefined};
      const expectedState = {loading: false, data: [], last_error: 'error'};
      const state = reducer(initialState, {
	type: types.COMIC_FAILED,
	err: {message: 'error'}
      })
      expect(state).to.deep.equal(expectedState);
    });
  })
})

