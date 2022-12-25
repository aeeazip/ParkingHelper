package ddwucom.mobile.ma02_20201019;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/* 즐겨찾기에 주차장 등록 */
public class RegisterFavorite extends AppCompatActivity {

    private EditText memo;
    private TextView addParkName, addParkAddress, addParkRating;
    private ImageView imageView;
    private ParkingDBHelper helper;
    private Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.favorite_add);

        result = (Result) getIntent().getSerializableExtra ("result");

        addParkName = findViewById (R.id.addParkName);
        addParkAddress = findViewById (R.id.addParkAddress);
        addParkRating = findViewById (R.id.addParkRating);
        imageView = findViewById (R.id.imageView);
        memo = findViewById (R.id.memo);

        addParkName.setText(result.getName());
        addParkAddress.setText (result.getAddress ());
        addParkRating.setText (result.getRating ());

        helper = new ParkingDBHelper (this);
    }
}