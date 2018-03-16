import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object MainKt {

    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun mainOld(args: Array<String>) {
        val pb = ProcessBuilder("ffmpeg", "-i", "http://10.8.0.3/live.m3u8", "-acodec", "copy", "-vcodec", "copy", "-f", "avi", "-bsf:v", "h264_mp4toannexb", "-")
        val p = pb.start()

        val inStream = p.errorStream

        Thread {
            val reader = InputStreamReader(inStream)
            val scan = Scanner(reader)
            while (scan.hasNextLine()) {
                println(scan.nextLine())
            }

        }.start()

        val writer = Thread {
            //ServletOutputStream stream = null;
            try {
                val stream = p.inputStream

                val file = File("/Users/zerogerc/file.avi")
                if (file.exists()) {
                    file.delete()
                }

                file.createNewFile()

                val output = FileOutputStream(file)
                val array = ByteArray(4096)
                var read = 0
                while (true) {
                    read = stream.read(array, 0, 4096)
                    if (read == -1) {
                        break
                    }
                    output.write(array, 0, read)
                }
                output.close()
            } catch (ignored: IOException) {
                ignored.printStackTrace()
            }
        }

        writer.start()

        val s = Scanner(System.`in`)
        while (true) {
            val l = s.nextLine()
            System.err.println(l)
            if (l == "q") {
                println("Q")
                p.outputStream.write('q'.toInt())
                p.outputStream.write('\n'.toInt())
                p.outputStream.flush()
                writer.join()
                return
            }
        }

    }
}