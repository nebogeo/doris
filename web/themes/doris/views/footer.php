			</div>
		</div>
		<!-- / main body -->

	</div>
	<!-- / wrapper -->

	<!-- footer -->
	<div id="footer" class="clearingfix">

		<div id="underfooter"></div>

		<!-- footer content -->
		<div class="rapidxwpr floatholder">

			<!-- footer credits -->
			<div class="footer-credits">
				Powered by the &nbsp;
				<a href="http://www.ushahidi.com/">
					<img src="<?php echo url::file_loc('img'); ?>media/img/footer-logo.png" alt="Ushahidi" style="vertical-align:middle" />
				</a>
				&nbsp; Platform
			</div>
			<!-- / footer credits -->



		</div>
		<!-- / footer content -->

	</div>
	<!-- / footer -->

	<?php
	echo $footer_block;
	// Action::main_footer - Add items before the </body> tag
	Event::run('ushahidi_action.main_footer');
	?>
</body>
</html>
