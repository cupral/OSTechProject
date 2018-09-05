const request = new XMLHttpRequest();
request.open("GET", 'http://haiku.ostechps.com/api?token=QF6rDyMMAcvMTGSEYfweKAGrbRBy297z&key=%E5%86%AC');
request.addEventListener("load",(event) => {
  console.log(event.target.status);
  console.log(event.target.responseText);
});
request.send();
