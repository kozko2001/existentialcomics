import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Link} from 'react-router';

export default class ComicCard extends Component {
  render() {
    const {dispatch, name, url, image, id} = this.props;
    let imageStyle = {
      background: 'url(' + image + ') center / cover',
      height: '200px',
      width: '200px'
    };
    return (
      <div className='mdl-cell mdl-cell--3-col'>
	<div className="mdl-card mdl-shadow--2dp">
	    <div className="mdl-card__title" style={imageStyle}>
		<h2 className="mdl-card__title-text">{name}</h2>
	    </div>
	    <div className="mdl-card__actions">
	      <Link
		className="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"
		to={'/detail/' + id}>
		See Strips
	      </Link>
	      <a
		className="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect"
		href={url}>
		Web
	      </a>
	    </div>
	    <div className="mdl-card__actions">
	    </div>
	</div>
    </div>
    );
  }
}
