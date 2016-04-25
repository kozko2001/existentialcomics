self.addEventListener('push', function(event) {

  var topics = JSON.parse(localStorage.getItem('topics')).join(',');
  event.waitUntil(
    self.registration.showNotification('ComicStrip', {
      body: 'Some of the comics ' + topics + ' has new strips'
    })
  );
});