import React, {Component} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as ListComicActions from '../actions/ListComicActions';
import styles from '../../css/index.scss';

class ListComics extends Component {
  render() {
    const {title, dispatch} = this.props;
    const actions = bindActionCreators(ListComicActions, dispatch);
    const titles = this.props.data.map((comic) => <div>{comic.comic_id}</div>);
    return (
      <main>
        <h1 className={styles.text}>Welcome {titles}!</h1>
        <button onClick={(e) => actions.fetchComics()}>
          Update Title
        </button>
      </main>
    );
  }
}

// this line means:
// pass the dispatch function of the store to the props of ListComics react component
// make the state of the listComicReducer as state of the component
// bind the listComicReducer state to the component, so each time it changes the component also changes
export default connect((state) => state.listComicReducer)(ListComics)
