import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Link, browserHistory } from 'react-router';
import RaisedButton from 'material-ui/RaisedButton';
import Card from 'material-ui/Card/Card';

import CardActions from 'material-ui/Card/CardActions';
import CardHeader from 'material-ui/Card/CardHeader';
import CardMedia from 'material-ui/Card/CardMedia';
import CardTitle from 'material-ui/Card/CardTitle';
import Paper from 'material-ui/Paper/Paper';
import FlatButton from 'material-ui/FlatButton';


export default class ComicCard extends Component {
  constructor(props) {
    super(props)
    this.state = {
      mouseOver: false
    }
  }
  
  onMouseEnter() {
    this.setState ({mouseOver: true});
  }

  onMouseLeave() {
    this.setState ({mouseOver: false});
  }

  onComicSelected() {
    browserHistory.push(`/detail/${this.props.id}`);
  }

  render() {
    const {dispatch, name, url, image, id} = this.props;

    let mouseOver = this.state.mouseOver || false;
    
    return (
	<div className='comic'>
	  <Paper zDepth={mouseOver? 4 : 1}
		 onMouseEnter={this.onMouseEnter.bind(this)}
		 onMouseLeave={this.onMouseLeave.bind(this)}>
	    <Card onClick={this.onComicSelected.bind(this)}>
	      <CardHeader
		title={name}
		/>
	      <CardMedia>
		<img src={image}  />
	      </CardMedia>
	      <CardActions>
		<RaisedButton label="See more strips" primary={true} />
	      </CardActions>
	    </Card>
	  </Paper>
	</div>
    );
  }
}
