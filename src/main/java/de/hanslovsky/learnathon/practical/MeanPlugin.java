package de.hanslovsky.learnathon.practical;

import java.io.File;
import java.io.IOException;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

@Plugin(type = Command.class, headless = true,
	menuPath = "File>New>Mean")
public class MeanPlugin implements Command {
	
	@Parameter
	private DatasetIOService datasetIOService;

	@Parameter
	private LogService logService;
	
	@Parameter
	private UIService ui;


	@Parameter
	private File file;

	@Override
	public void run() {
		try {
			Dataset image = datasetIOService.open(file.getAbsolutePath());
			double sum = 0.0;
			long count = 0;
			for ( Cursor<RealType<?>> cursor = Views.flatIterable(image).cursor(); cursor.hasNext(); ) {
				double val = cursor.next().getRealDouble();
				if ( !Double.isNaN(val) ) {
					sum += val;
					++count;
				}
			}
			ui.showDialog( "Mean: " + ( sum /= count ) );
		}
		catch (final IOException exc) {
			// Use the LogService to report the error.
			logService.error(exc);
		}
	}
	
	public static void main(final String... args) throws Exception {
		// Launch ImageJ as usual.
		ImageJ ij = new ImageJ();
		ij.launch(args);

		// Launch the "OpenImage" command.
		ij.command().run( MeanPlugin.class, true );
	}

}
