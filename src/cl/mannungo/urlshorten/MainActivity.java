package cl.mannungo.urlshorten;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if( intent == null ) {
			finish();
		} else {
			Uri url = intent.getData();
			if( url == null ) {
				finish();
			} else {
				System.out.println("mortega -- "+url);
				Toast.makeText(getApplicationContext(), "Resolving "+url+"...", Toast.LENGTH_SHORT).show();
				new ResolveUri(this).execute(url.toString());
			}
		}
	}

	private class ResolveUri extends AsyncTask<String, Integer, String> {
		Activity activity;
		String url_o;

		public ResolveUri(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected String doInBackground(String... urls) {
			String url = urls[0];
			String location;
			HttpURLConnection c;
			URL u;
			for(int i=0; i < 10; i++) {
				try {
					u = new URL(url);
					c = (HttpURLConnection) u.openConnection();
					c.setRequestMethod("HEAD");
					c.setInstanceFollowRedirects(true);
					c.connect();

					location = c.getHeaderField("Location");
					if(location == null) return c.getURL().toString();
					url = location.trim();


				} catch (MalformedURLException e) {
					url = null;
				} catch (IOException e) {
					url = null;
				}
			}

			return url;
		}

		@Override
		protected void onPostExecute(String url) {
			System.out.println("mannungo ==> "+url);
			if( url == null || url.equals(url_o) ) {
				Toast.makeText(getApplicationContext(), "No pude resolver la url", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "=> "+url, Toast.LENGTH_SHORT).show();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}

			this.activity.finish();
		}
	}
}
