package com.example.a1;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

public class HomeFragment extends Fragment {

    private ImageView imgView;
    private TextView tv;
    private Uri imageUri;
    private Bitmap bitmap;
    private Interpreter tflite;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            if (imageUri != null) {
                                try {
                                    bitmap = BitmapFactory.decodeStream(requireActivity().getContentResolver().openInputStream(imageUri));
                                    imgView.setImageBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
        );

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imgView = view.findViewById(R.id.imageView);
        tv = view.findViewById(R.id.text_home);
        Button select = view.findViewById(R.id.button);
        Button predict = view.findViewById(R.id.button2);




        select.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });

        predict.setOnClickListener(v -> {
            if (bitmap != null) {
                // Convert the bitmap to a ByteBuffer
                ByteBuffer inputBuffer = convertBitmapToByteBuffer(bitmap);

                // Create the output tensor buffer
                TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 6}, DataType.FLOAT32);

                // Run inference
                tflite.run(inputBuffer, outputBuffer.getBuffer());

                // Get the predicted class and confidence
                float[] outputArray = outputBuffer.getFloatArray();
                String[] classNames = {"Arborio", "Basmati", "Invalid_Image", "Ipsala", "Jasmine", "Karacadag"};

                int predictedClassIndex = argmax(outputArray);
                String predictedClass = classNames[predictedClassIndex];
                float confidence = outputArray[predictedClassIndex] * 100; // Convert confidence to percentage

                // Format the confidence value to two decimal places
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String formattedConfidence = decimalFormat.format(confidence);

                // Display the predicted result
                String predictionText = "" + predictedClass +
                        "\nConfidence: " + formattedConfidence + "%";
                tv.setText(predictionText);

            } else {
                Toast.makeText(requireContext(), "Please select an image first.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        int inputWidth = 224;
        int inputHeight = 224;
        int numChannels = 3;
        int bufferSize = inputWidth * inputHeight * numChannels * 4; // 4 bytes per float

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);
        byteBuffer.order(ByteOrder.nativeOrder());

        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(bitmap);
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(inputHeight, inputWidth, ResizeOp.ResizeMethod.BILINEAR))
                .add(new NormalizeOp(0.0f, 255.0f))
                .build();
        tensorImage = imageProcessor.process(tensorImage);
        byteBuffer = tensorImage.getBuffer();


        return byteBuffer;
    }
    private MappedByteBuffer loadModelFile() throws IOException {
        try (AssetFileDescriptor fileDescriptor = requireContext().getAssets().openFd("model16.tflite");
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
             FileChannel fileChannel = inputStream.getChannel()) {
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private int argmax(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
