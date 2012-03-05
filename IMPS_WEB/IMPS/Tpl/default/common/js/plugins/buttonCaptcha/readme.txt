==================================================================================
jQuery.buttonCaptcha : How To ////////////////////////////////////////////////////|
==================================================================================

	jQuery.buttonCaptcha - plugin that protects your site from robots using jQuery.
	Version: 1.1

	Copyright 2011, Sergey Kamardin.

	http://www.gobwas.com/bcaptcha


	Licensed under the MIT license.
	http://www.opensource.org/licenses/mit-license.php

	Date: 		Mon Jun 7 17:15:07 2011 +0300.
	Location: 	Moscow, Russia.
	Contact: 	gobwas@gobwas.com

==================================================================================
What do I need for use it? //////////////////////////////////////////////////////|
==================================================================================

	Thanks for using jQuery.buttonCaptcha!

	To use it on your perfect site you need five things:

		a) jquery library file (1.3.0 and higher);
		b) jquery ui library file (1.8.9 and higher);
		c) jquery.buttonCaptcha.js file;
		d) jquery.buttonCaptcha.styles.css file;
		e) folder named 'images' with 14 small pics.

	So, to install it on your site, just follow these simple steps:

	=================
	STEP 1		|
	=================
	
	Save jquery.buttonCaptcha.js, jquery.buttonCaptcha.styles.css, and 'images' folder to the one directory in some place on your site.
	Remember, that all files of buttonCaptcha must be saved in ONE directory, if not - you must edit .css file.

	For example:
	
	I've put 'buttonCaptcha' folder into js/plugins directory on my site. In the next step I show how it looks inside links.

	=================
	STEP 2		|
	=================

	Put these rows in the <head> tag:

	<head>
		<link rel="stylesheet" type="text/css" href="/js/plugins/buttonCaptcha/jquery.buttonCaptcha.styles.css" />

		<script type="text/javascript" language="javascript" src="/js/jquery/jquery-1.5.1.min.js"></script>
		<script type="text/javascript" language="javascript" src="/js/jquery/jquery-ui-1.8.10.custom.min.js"></script>
		<script type="text/javascript" language="javascript" src="/js/plugins/buttonCaptcha/jquery.buttonCaptcha.min.js"></script>
	</head>

	
	=================
	STEP 3		|
	=================

	To finish installation you need to run .buttonCaptcha() function on some button at your site.
	
	You don't need to have some form on site, but for full tutorial i'll show it in this example:

	<html>
		<body>
			<form id="form" action="..." enctype="multipart/form-data">
				<input type="text" name="name" />
				<textarea name="area">Hello!</textarea>
				<button id="button" type="submit">Captcha!</button>
			</form>

	// It is a simple html-code below. Now, in some script you must run .buttonCaptcha() function on button:

			<script type="text/javascript">
				$(document).ready(function() {
					$("#button").buttonCaptcha();
				}
			</script>
		</body>
	</html>

	You've got it!
	
	Remember, that you can run .buttonCaptcha() on many buttons in one time - for example:

	$('button').buttonCaptcha() - will protects all buttons in document. If you have 10 buttons - it will be 10 captchas before each button.
	
	I think you are ready to tune your Captcha - to set the code word, domain zone, and few other options.

	=================
	Tuning		|
	=================

	You also can run .buttonCaptcha() with some options.
	Here listing of it default values:

	var options = {
		codeWord		:	'gbws',		// code word that must be assembled from pieces;
		codeZone		:	'com',		// if you want, you can add a domain zone to your code word (length must be from 2 to 4, without dot);
		hideButton		:	true,		// if it true, button, which you passed to the buttonCaptcha will be hidden till unlock;
		hideCaptcha		:	false,		// if it true, Captcha will fade out when unlocked;
		lockButton		:	true,		// if it true, button will be disabled till unlock;
		scrollToButton		: 	false,		// Need to use a jQuery.scrollTo plugin (google knows where); if it true, when Captcha unlocked, will be autoscroll to the button.
		verifyInput		:	true,		// if it true, to the first parent form will be attached a hidden field with the value of deferred letters.
		verifyName		:	'gbws_captcha_input',	// the name of hidden field;
		verifyMustBe		:	false,		// if true, then to the first parent form will be attached a hidden field with the needed value of captcha to be unlocked. ******* (v1.1)
		verifyMustName		:	'gbws_captcha_must_input',	// the name of hidden field; ******* (v1.1)
		captchaHeader		:	'Are you a robot?',	// question above the Captcha;
		captchaTip		: 	'Drag letters from left to right, to get word "%code_word%". Thanks!' 	// tip text; remember that you must save %code_word% tag! 																								// For example : < Hello, make this word: %code_word% >
		captchaUnlocked		:	'Unlocked!'	// text for the header of captcha when it unlocked;
	}

	So, you can change some options by giving it in object, which contains defenitions of various which you need:
	
	$('#form button').buttonCaptcha({
		codeWord:'mysite',
		codeZone:'com',
		captchaHeader:'Humanoid?',
		captchaTip:'Just drag letters!',
		verifyInput:true,
		verifyName:'hidden_field_my_site',
		verifyMustBe:true,
		verifyMustName:'hidden_field_real_my_site'
	});
	
	In version 1.1 added functionality of randomizing captcha when refreshing page. To use this possibility, set codeWord as number. Just like this:
	

	$('#form button').buttonCaptcha({
		codeWord:5,
		codeZone:false
	});

	The number, that you give to codeWord - it is a number of letters in randomized codeWord.
	

	Ok! Sorry for my english, enjoy buttonCaptcha and if you'll need help - contact me!

==================================================================================
Where can i get jquery libraries? ////////////////////////////////////////////////|
==================================================================================

	Just follow this links:

	http://jqueryui.com/
	http://jquery.com/

==================================================================================
jQuery.buttonCaptcha : Change List////////////////////////////////////////////////|
==================================================================================

25.04.2011// 1.0 beta -> 1.01 : Added compatibility with jQuery 1.3.0.
06.06.2011// 1.01 -> 1.02 : Bug fixed in situation when you have several identical letters in the code word.
07.06.2011// 1.02 -> 1.1 : Added functionality: 	a) 	Now it available to use numbers in codeWord;
							b) 	Added possibility to randomize codeWord in each refresh - you must set codeWord as number (without quotes);
							c) 	Added possibility to generate hidden input with needed value of buttonCaptcha - 
								for comparing values of hidden inputs (options verify and verifyMustBe) in server php scripts.

