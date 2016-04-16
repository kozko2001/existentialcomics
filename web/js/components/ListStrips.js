import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Link} from 'react-router';
import * as ListStripActions from '../actions/ListStripActions';
import {bindActionCreators} from 'redux'
import LazyLoad from 'react-lazyload';
import FlatButton from 'material-ui/FlatButton';
import AppBar from 'material-ui/AppBar';
import IconButton from 'material-ui/IconButton';
import ArrowBack from 'material-ui/svg-icons/navigation/arrow-back';

class ListStrips extends Component {

  componentDidMount() {
    const {dispatch} = this.props;
    const {comicId} = this.props.params;

    this.actions = bindActionCreators(ListStripActions, dispatch);
    this.actions.fetchStrips(comicId);
  }

  getImageUrl(strip) {
    return `http://comic.allocsoc.net/comics/image/${strip._id}`
  }

  getCurrentStripIndex() {
    let strips = this.props.data;
    return strips.findIndex((strip) => strip === this.props.selected);
  }

  actionGoNewer(e) {
    let index = this.getCurrentStripIndex();
    let strip = this.props.data[index-1]
    this.actions.onChangeStripSelected(strip);
  }

  actionGoLater(e) {
    let index = this.getCurrentStripIndex();
    let strip = this.props.data[index+1]
    this.actions.onChangeStripSelected(strip);
  }

  actionGoRandom(e) {
    let strips = this.props.data;
    let index = Math.floor(Math.random()*strips.length);
    let strip = this.props.data[index]
    this.actions.onChangeStripSelected(strip);
  }
  
  render() {
    let currentStripIndex = this.getCurrentStripIndex()

    if(currentStripIndex >= 0) {
      let canGoLater = (currentStripIndex + 1) < this.props.data.length 
      let canGoNewer = currentStripIndex > 0 
      let strip = this.props.data[currentStripIndex];

      let goNewer = () => this.actionGoNewer();
      let goLater = () => this.actionGoLater();
      let goRandom = () => this.actionGoRandom();
      let goBackToStrips = () => location.href = '/';
      let goToOriginalSite = () => window.open(strip.url, '_blank');
      
      return (
	<main>
	  <AppBar
	    title={strip.title}
	    iconElementLeft={<IconButton onClick={goBackToStrips}><ArrowBack/></IconButton>}
	    iconElementRight={<FlatButton label='Go to original site' onClick={goToOriginalSite} />}
	    /> 
	  <div className='strips'>
	    <div className='toolbar'>
	      <FlatButton disabled={!canGoNewer} primary={true} onClick={goNewer}>
		&lt;
	      </FlatButton>
	      <FlatButton primary={true} onClick={goRandom}>
		Random
	      </FlatButton>
	      <FlatButton disabled={!canGoLater} primary={true} onClick={goLater}>
		&gt;
	      </FlatButton>
	    </div>
	    <div>
	      <img src={this.getImageUrl(strip)}/>
	    </div>
	  </div>
	</main>
      )
    } else {
      return (<div>
	      </div>);
    }
  }
}

export default connect((state) => state.listStripReducer)(ListStrips)
