package in.snotes.snotes.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.marcoscg.easylicensesdialog.EasyLicensesDialogCompat;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.snotes.snotes.R;
import in.snotes.snotes.utils.Utils;

public class AboutActivity extends AppCompatActivity implements AboutAdapter.AboutListener {

    @BindView(R.id.toolbar_about)
    Toolbar toolbarAbout;
    @BindView(R.id.rv_about)
    RecyclerView rvAbout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarAbout);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvAbout.setLayoutManager(layoutManager);

        List<Object> aboutItems = Utils.getAboutItems(this);

        AboutAdapter adapter = new AboutAdapter(this, this, aboutItems);
        rvAbout.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(String title) {

        final String RATE = this.getString(R.string.rate_title);
        final String LICENSE = this.getString(R.string.licenses);
        final String SHARE = this.getString(R.string.share_title);
        final String GITHUB = this.getString(R.string.follow_on_github);
        final String REPORT = this.getString(R.string.report_title);
        final String DONATE = this.getString(R.string.donate);

        if (Objects.equals(title, RATE)) {
            takeThemToGooglePlay();
        } else if (Objects.equals(title, LICENSE)) {
            showLicense();
        } else if (Objects.equals(title, SHARE)) {
            shareAppLink();
        } else if (Objects.equals(title, GITHUB)) {
            takeToGithub();
        } else if (Objects.equals(title, REPORT)) {
            takeToFeedback();
        } else if (Objects.equals(title, DONATE)) {
            showDonateDialog();
        }
    }

    private void showDonateDialog() {
        Toast.makeText(this, "Donate", Toast.LENGTH_SHORT).show();
    }

    private void takeToFeedback() {
        Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show();
    }

    private void takeToGithub() {
        String githubLink = "https://www.github.com/sriramr98";
        Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubLink));
        startActivity(githubIntent);
    }

    private void shareAppLink() {
        Toast.makeText(this, "Link", Toast.LENGTH_SHORT).show();
    }

    private void showLicense() {
        new EasyLicensesDialogCompat(this)
                .setTitle("Licenses")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void takeThemToGooglePlay() {
        Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
    }
}
