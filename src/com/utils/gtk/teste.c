#include "com_utils_gtk_NativeGtkSystemTray.h"
#include <jni.h>
#include  <stdlib.h>
#include <gtk/gtk.h>

GtkWidget *menu;
GtkWidget* itens[];

static void trayView(GtkMenuItem *item, gpointer user_data);
static void trayExit(GtkMenuItem *item, gpointer user_data);
static void trayIconActivated(GObject *trayIcon, gpointer data);
static void trayIconPopup(GtkStatusIcon *status_icon, guint button, guint32 activate_time, gpointer popUpMenu);
static void destroy (GtkWidget*, gpointer);
static gboolean delete_event (GtkWidget*, GdkEvent*, gpointer);
static gboolean window_state_event (GtkWidget *widget, GdkEventWindowState *event, gpointer user_data);



JNIEXPORT void JNICALL Java_com_utils_gtk_NativeGtkSystemTray_addMenuItem
  (JNIEnv *env, jobject obj, jstring text, jint id){
    
    jclass cls = (*env)->FindClass(env, "com/utils/gtk/NativeGtkSystemTray");
    if(cls !=0) { 
        jmethodID mid = (*env)->GetStaticMethodID(env, cls, "menuAction", "(I)V");
        if(mid !=0){ 
            (*env)->CallStaticVoidMethod(env, cls, mid, id);
        }
     }
}


int main(int argc, char const *argv[])
{
	
	return 0;
}


