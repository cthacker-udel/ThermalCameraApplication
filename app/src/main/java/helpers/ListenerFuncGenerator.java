package helpers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.function.Function;

/**
 * Class for generating listener functions
 */
public class ListenerFuncGenerator {

    /**
     * Generates a success listener function given a lambda function that takes in the type specified
     *
     * @param successFunction - The function that is called when the success listener is executing
     * @param <I> - The type of the value the successFunction will be receiving and the success listener will be executing with
     * @return - The SuccessListener instance
     */
    public static <I> OnSuccessListener<I> generateOnSuccessListener(Function<I, Void> successFunction) {
        return new OnSuccessListener<I>() {
            @Override
            public void onSuccess(I i) {
                successFunction.apply(i);
            }
        };
    }

    /**
     * Generates a failure listener function, given a lambda representing the function that will fire on the failure
     *
     * @param failureFunction - The custom function that fires when the failure is captured and executed
     * @return The generated failure function
     */
    public static OnFailureListener generateFailureListener(Function<Exception, Void> failureFunction) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                failureFunction.apply(e);
            }
        };
    }

}
