import React, {Component} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as ListComicActions from '../actions/ListComicActions';
import styles from '../../css/index.scss';
import {Link} from 'react-router';
import ComicCard from './ComicCard';

class ListComics extends Component {
  componentDidMount() {
    const {dispatch} = this.props;
    const actions = bindActionCreators(ListComicActions, dispatch);
    actions.fetchComics();
  }
  
  render() {
    return (
      <main>

	<div>
	  {
	    this.props.data.map((comic) =>
				<ComicCard dispatch={this.props.dispatch}
					     name={comic.name}
					     url={comic.url}
					     id={comic.comic_id}
					     key={comic.comic_id}
					     image={comic.image} />)
	  }
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
