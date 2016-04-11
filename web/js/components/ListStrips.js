import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Link} from 'react-router';

class ListStrips extends Component {

  componentDidMount() {
    console.log('mounted?');
  }
  
  render() {
    return (
	<ul>
	<li> {this.props.params.comicId} </li>
	</ul>
    );
  }
}

export default ListStrips;
