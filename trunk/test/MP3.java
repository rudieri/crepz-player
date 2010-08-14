
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagOptionSingleton;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author manchini
 */
public class MP3 {

    public static void main(String[] args) {
        try {
            MP3File mp3 = new MP3File(new File("C:/JPlayer/teste2.mp3"));
            TagOptionSingleton.getInstance().setDefaultSaveMode(TagConstant.MP3_FILE_SAVE_OVERWRITE);

//            // setup id3v1
//            ID3v1 id3v1 = mp3.getID3v1Tag();
//            Iterator t = id3v1.iterator();
//            id3v1.setTitle("Oi2");
//            mp3.setID3v1Tag(id3v1);


            mp3.getID3v2Tag().setAlbumTitle("owwwww");



//            // setup id3v2
//            AbstractID3v2Frame frame;
//            AbstractID3v2FrameBody frameBody;
//            frameBody = new FrameBodyTALB((byte) 0, "albumTitle");
//            frame = new ID3v2_4Frame(frameBody);

//            mp3.getID3v2Tag().setFrame(frame);
//
//            // setup lyrics3v2
//            AbstractLyrics3 lyrics3 = mp3.getLyrics3Tag();
//
//            Lyrics3v2Field field;
//            AbstractLyrics3v2FieldBody fieldBody;
//            fieldBody = new FieldBodyEAL("albumTitle");
//            field = new Lyrics3v2Field(fieldBody);
//            lyrics3.setField(field);
//
            // setup filename tag
//            frameBody = new FrameBodyTALB((byte) 0, "albumTitle");
//            frame = new ID3v2_4Frame(frameBody);
//            filenameId3.setFrame(frame);
            TagOptionSingleton.getInstance().setFilenameTagSave(true);

            mp3.save();
            


            System.out.println(mp3.toString());
        } catch (Exception ex) {
            Logger.getLogger(MP3.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
