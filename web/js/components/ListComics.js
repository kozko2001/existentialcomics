import React, {Component} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as ListComicActions from '../actions/ListComicActions';
import styles from '../../css/index.scss';
import {Link} from 'react-router';
import ComicCard from './ComicCard';

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
	<li><Link to="/detail">Detail</Link></li>

      <div className='mdl-grid'>
      {this.props.data.map((comic) =>
			   <ComicCard
				dispatch={dispatch}
				name={comic.name}
				url={comic.url}
				image={comic.image} />)}
      </div>
      </main>
    );
  }
}

// this line means:
// pass the dispatch function of the store to the props of ListComics react component
// make the state of the listComicReducer as state of the component
// bind the listComicReducer state to the component, so each time it changes the component also changes
export default connect((state) => state.listComicReducer)(ListComics)
