package com.example.vaibhav.prathamquizzingapp.classes;

/**
 * Created by vaibhav on 26/6/18.
 */

public class audios {

//    private final static int AUDIO_REQUEST=5;
//    private StorageReference storageReference;
//    private MediaPlayer mediaPlayer;
//    private Uri uriAudio;


    //mediaPlayer = new MediaPlayer();
    //storageReference = FirebaseStorage.getInstance().getReference();

         /*             String downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                String filename = "14.mp3";
                MediaPlayer mediaPlayer = new MediaPlayer();
                try{
                    mediaPlayer.setDataSource(downloads+"/"+filename);
                    mediaPlayer.prepare();
                }catch (Exception e) { e.printStackTrace();}
                //mediaPlayer.start();
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,AUDIO_REQUEST);
   */

       /*StorageReference ref = storageReference.child("audios/"+System.currentTimeMillis()+"."+getFileExtension(uriAudio));
                ref.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        databaseReference.child("audio").setValue(taskSnapshot.getDownloadUrl().toString());
                        }
                })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
       ;*/

       /*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==AUDIO_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null){
         uriAudio = data.getData();
         try {
             mediaPlayer.setDataSource(getApplicationContext(), uriAudio);
             mediaPlayer.prepareAsync();
             mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                 @Override
                 public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                     Log.d(TAG, "onError: mp"+i+i1);
                     return false;
                 }
             });
             mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                 @Override
                 public void onPrepared(final MediaPlayer mediaPlayer) {
                     Log.d(TAG, "onPrepared: Preppaarreedd");
                     CountDownTimer cntr_aCounter = new CountDownTimer(3000, 1000) {
                         public void onTick(long millisUntilFinished) {

                             mediaPlayer.start();
                         }

                         public void onFinish() {
                             //code fire after finish
                             mediaPlayer.stop();
                         }
                     };cntr_aCounter.start();
                 }
             });
         }catch (IOException e){
             e.printStackTrace();
         }catch (Exception e){
             e.printStackTrace();
         }
        }
    }

    private String getFileExtension(Uri uriAudio) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriAudio));
    }

*/
}
