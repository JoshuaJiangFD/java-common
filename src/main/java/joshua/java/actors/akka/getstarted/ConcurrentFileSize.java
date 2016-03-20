package joshua.java.actors.akka.getstarted;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * The example of concurrent file size calculation using akka actors.
 * <p/>
 * Created by joshua on 2016/3/20.
 */
public class ConcurrentFileSize {
}


class FileProcessor extends UntypedActor {
    private final ActorRef sizeCollector;

    public FileProcessor(ActorRef sizeCollector) {
        this.sizeCollector = sizeCollector;
    }

    @Override
    public void preStart() throws Exception {

    }

    public void registerToGetFile() {
        sizeCollector
    }

    @Override
    public void onReceive(Object o) throws Exception {

    }
}

class SizeCollector extends UntypedActor {
    private final List<String> toProcessFileNames = Lists.newArrayList();
    private final List<ActorRef> idleFileProcessors = Lists.newArrayList();
    private long pendingNumberOfFilesToVisit = 0L;
    private long totalSize = 0L;
    private long start = System.nanoTime();

    public void sendAFileToProcess () {
        if (!toProcessFileNames.isEmpty() && !idleFileProcessors.isEmpty()) {

        }
    }

    @Override
    public void onReceive(Object o) throws Exception {

    }
}

/**
 * The message to
 */
class RequestAFile {

}

class FileSize {
    public final long size;

    public FileSize(
            final long fileSize) {
        size = fileSize;
    }
}

class FileToProcess {
    public final String fileName;

    public FileToProcess(final String name) {
        fileName = name;
    }
}