package joshua.java.actors.akka.getstarted;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.io.File;
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
        registerToGetFile();
    }

    /**
     * tell the SizeCollector that this FileProcessor is ready to take new task.
     */
    public void registerToGetFile() {
        // send a message to sizeCollector, and use calling actor as sender reference.
        sizeCollector.tell(new RequestAFile(), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        FileToProcess fileToProcess = (FileToProcess) message;
        final File file = new File(fileToProcess.fileName);
        long size = 0L;
        if (file.isFile()) {
            size = file.length();
        } else {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isFile()) {
                        size += child.length();
                    } else {
                        sizeCollector.tell(new FileToProcess(child.getPath()), ActorRef.noSender());
                    }
                }
            }
        }
        sizeCollector.tell(new FileSize(size), ActorRef.noSender());
        registerToGetFile();
    }
}

class SizeCollector extends UntypedActor {

    private static final Logger log  = Logger.getLogger(SizeCollector.class);
    private final List<String> toProcessFileNames = Lists.newArrayList();
    private final List<ActorRef> idleFileProcessors = Lists.newArrayList();
    private long pendingNumberOfFilesToVisit = 0L;
    private long totalSize = 0L;
    private long start = System.nanoTime();

    /**
     * Dispatch directories to idling FileProcessor to explore.
     */
    public void sendAFileToProcess() {
        if (!toProcessFileNames.isEmpty() && !idleFileProcessors.isEmpty()) {
            idleFileProcessors.remove(0).tell(new FileToProcess(toProcessFileNames.remove(0)), ActorRef.noSender());
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof RequestAFile) {
            idleFileProcessors.add(getSender());
            sendAFileToProcess();
        }
        if (message instanceof FileToProcess) {
            toProcessFileNames.add(((FileToProcess) message).fileName);
            pendingNumberOfFilesToVisit += 1;
            sendAFileToProcess();
        }
        if (message instanceof FileSize) {
            totalSize += ((FileSize) message).size;
            pendingNumberOfFilesToVisit -= 1;

            if (pendingNumberOfFilesToVisit == 0) {
                long end = System.nanoTime();
                log.info("The total size is " + totalSize);
                log.info("Time taken is " + (end - start) / 1.0e9);
                // stop all actors from any actor inside.
                context().system().shutdown();
            }
        }
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