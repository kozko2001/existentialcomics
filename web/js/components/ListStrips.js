import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Link, hashHistory} from 'react-router';
import * as ListStripActions from '../actions/ListStripActions';
import {bindActionCreators} from 'redux'
import LazyLoad from 'react-lazyload';
import FlatButton from 'material-ui/FlatButton';
import AppBar from 'material-ui/AppBar';
import IconButton from 'material-ui/IconButton';
import ArrowBack from 'material-ui/svg-icons/navigation/arrow-back';
import {subscribe, unsubscribe} from '../actions/PushActions'; 
import {API_ENDPOINT} from '../constants/Constants.js';

class ListStrips extends Component {

  componentDidMount() {
    const {dispatch} = this.props;
    const {comicId, stripId} = this.props.params;

    this.actions = bindActionCreators(ListStripActions, dispatch);
    this.actions.fetchStrips(comicId, stripId);
  }

  componentWillReceiveProps(nextProps) {
    console.log(nextProps)
    const {stripId, comicId} = nextProps.params;
    const selected = nextProps.selected;

    if(selected && stripId != selected._id) {
      hashHistory.push(`/detail/${comicId}/${selected._id}`);
    }
  }

  getImageUrl(strip) {
    return `${API_ENDPOINT}comics/image/${strip._id}`
  }

  getCurrentStripIndex() {
    let strips = this.props.data;
    let stripId = this.props.params.stripId;

    if(strips && stripId) {
      return strips.findIndex((strip) => strip._id === stripId);
    } else {
      return -1;
    }
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

  subscribeButton() {

    let comic_id = this.props.params.comicId;
    let comics_subscribed = this.props.topics;
    let is = comics_subscribed.filter((t) => t === comic_id).length > 0
    
    let text = is ? 'Unsubscribe' : 'Subscribe';
    let action = () => { is ? unsubscribe(comic_id) : subscribe(comic_id)} 

    return (
      <FlatButton onClick={action} label={text} />
    )
  }
  
  
  render() {
    let currentStripIndex = this.getCurrentStripIndex();
    let goBackToStrips = () => hashHistory.push('/');
    let goToOriginalSite = () => window.open(strip ? strip.url : '', '_blank');
    let strip = this.props.data[currentStripIndex];

    var viewer = (<div> </div>)

    if(currentStripIndex >= 0) {
      let canGoLater = (currentStripIndex + 1) < this.props.data.length 
      let canGoNewer = currentStripIndex > 0 

      let goNewer = () => this.actionGoNewer();
      let goLater = () => this.actionGoLater();
      let goRandom = () => this.actionGoRandom();
      
      viewer = (
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
      );
    }

    return (<main>
	    <AppBar
	    title={strip ? strip.title : ''}
	    iconElementLeft={<IconButton onClick={goBackToStrips}><ArrowBack/></IconButton>}
	    iconElementRight={this.subscribeButton()}
	    /> 
	    {viewer}
	    </main>);
  }
}

export default connect((state) => {
  return {...state.listStripReducer, topics: state.pushReducer.topics}
})(ListStrips)
