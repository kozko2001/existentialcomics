import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Link} from 'react-router';
import * as ListStripActions from '../actions/ListStripActions';
import {bindActionCreators} from 'redux'

class ListStrips extends Component {

  componentDidMount() {
    const {dispatch} = this.props;
    const {comicId} = this.props.params;
    const actions = bindActionCreators(ListStripActions, dispatch);
    actions.fetchStrips(comicId);
  }
  
  render() {
    const {comicId} = this.props.params;
    const strips = this.props.data[comicId];
    return (
	<ul>
	<li> {comicId} </li>
	{
	  strips.map((strip) =>
		     <img src={'http://comic.allocsoc.net/comics/thumbnail/' + strip._id} />)
	}
	</ul>
    );
  }
}

export default connect((state) => state.listStripReducer)(ListStrips)
