var express = require('express');
var https = require('https');
var http = require('http');
var fs = require('fs');
var stripe = require("stripe")(process.env("STRIPE_SECRET"))
var bodyParser = require('body-parser')
var timeout = require('connect-timeout');

var port = 443;

var options = {
	key: fs.readFileSync('./ssl/server.key'),
	cert: fs.readFileSync('./ssl/server.crt'),
	ca: fs.readFileSync('./ssl/ca.crt'),
	requestCert: true,
	rejectUnauthorized: false,
}; 

var app = express()
var jsonParser = bodyParser.json()

app.post('/', jsonParser, timeout('10s'), function(req, res, next) {
	if (req.timedout) return next(createError(503, 'Response timeout'));
	if (!req.body) return res.sendStatus(400).send("Invalid json");
	console.log(req.body);
	var charge = stripe.charges.create(
	{
		amount: req.body.amount,
		currency: "usd",
		source: req.body.id,
		description: "Self Checkout App"
	},
	function(err, charge) {
		// The card has been declined
		if (err && err.type === 'StripeCardError') {
			console.log('Card declined: ' + err.message);
			return res.send('Card declined');
		}
		else if (err) {
			console.log('Unknown error occurred:' + err.type + ": " + err.message);
			return res.send('Unknown error occurred');
		}
		else {
			console.log('Success!');
			return res.send('Success!');
		}
	});
	return;
});

var errorFilter = function(err, req, res, next) {
	logger.warn(err.stack);
	if (!res.headersSent) {
		errcode = err.status || 500;
		msg = err.message || 'server error!';
		res.status(errcode).send(msg);
	}
};

app.use(errorFilter);

// This line is from the Node.js HTTPS documentation.
var secureServer = https.createServer(options, app).listen(port, function() {
	console.log("Secure Express server listening on port " + port);
});
