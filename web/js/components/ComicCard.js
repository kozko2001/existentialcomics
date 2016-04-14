import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Link, browserHistory } from 'react-router';
import RaisedButton from 'material-ui/lib/raised-button';
import Card from 'material-ui/lib/card/card';
import CardActions from 'material-ui/lib/card/card-actions';
import CardHeader from 'material-ui/lib/card/card-header';
import CardMedia from 'material-ui/lib/card/card-media';
import CardTitle from 'material-ui/lib/card/card-title';
import Paper from 'material-ui/lib/paper';
import FlatButton from 'material-ui/lib/flat-button';


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
		<RaisedButton label="See more strips" primary={true} style={style}/>
	      </CardActions>
	    </Card>
	  </Paper>
	</div>
    );
  }
}
