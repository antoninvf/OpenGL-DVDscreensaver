package educanet;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Square {

    private float x;
    private float y;
    private float z;

    public float[] vertices;

    private float[] fuckingcolors = {
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
    };

    private static final float[] textures = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };

    private final int[] indices = {
            0, 1, 3, // First triangle
            1, 2, 3 // Second triangle
    };

    private static int squareVaoId;
    private static int squareVboId;
    private static int squareEboId;
    private static int squareColorId;
    private FloatBuffer cb;
    private static int uniformMatrixLocation;
    public Matrix4f matrix;
    public FloatBuffer matrixFloatBuffer;
    private static int textureIndicesId;

    private static int textureId;

    public Square(float x, float y, float length) {
        this.x = x;
        this.y = y;
        this.z = length;
        float[] vertices = {
                x + length, y, 0.0f, // 0 -> Top right
                x + length, y - length, 0.0f, // 1 -> Bottom right
                x, y - length, 0.0f, // 2 -> Bottom left
                x, y, 0.0f, // 3 -> Top left
        };
        matrix = new Matrix4f()
                .identity();
        // 4x4 -> FloatBuffer of size 16
        matrixFloatBuffer = BufferUtils.createFloatBuffer(16);

        this.vertices = vertices;
        cb = BufferUtils.createFloatBuffer(fuckingcolors.length).put(fuckingcolors).flip();

        // Generate all the ids
        squareVaoId = GL33.glGenVertexArrays();
        squareVboId = GL33.glGenBuffers();
        squareEboId = GL33.glGenBuffers();
        squareColorId = GL33.glGenBuffers();
        textureIndicesId = GL33.glGenBuffers();

        textureId = GL33.glGenTextures();
        loadImage();

        // Get uniform location
        uniformMatrixLocation = GL33.glGetUniformLocation(Shaders.shaderProgramId, "matrix");

        // Tell OpenGL we are currently using this object (vaoId)
        GL33.glBindVertexArray(squareVaoId);

        // Tell OpenGL we are currently writing to this buffer (eboId)
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, squareEboId);
        IntBuffer ib = BufferUtils.createIntBuffer(indices.length)
                .put(indices)
                .flip();
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);

        // colors
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareColorId);
        FloatBuffer cb = BufferUtils.createFloatBuffer(fuckingcolors.length)
                .put(fuckingcolors)
                .flip();

        // Send the buffer (positions) to the GPU
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, cb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(1);

        // Change to VBOs...
        // Tell OpenGL we are currently writing to this buffer (vboId)
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareVboId);
        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length)
                .put(vertices)
                .flip();

        // Send the buffer (positions) to the GPU
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureIndicesId);

        FloatBuffer tb = BufferUtils.createFloatBuffer(textures.length)
                .put(textures)
                .flip();

        // Send the buffer (positions) to the GPU
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, tb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(2);

        GL33.glUseProgram(educanet.Shaders.shaderProgramId);

        // Sending Mat4 to GPU
        matrix.get(matrixFloatBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixFloatBuffer);

        // Clear the buffer from the memory (it's saved now on the GPU, no need for it here)
        MemoryUtil.memFree(fb);
        MemoryUtil.memFree(cb);
        MemoryUtil.memFree(tb);
        MemoryUtil.memFree(ib);
    }

    public void render() {
        GL33.glUseProgram(Shaders.shaderProgramId);
        matrix.get(matrixFloatBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixFloatBuffer);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
        GL33.glBindVertexArray(squareVaoId);
        GL33.glDrawElements(GL33.GL_TRIANGLES, indices.length, GL33.GL_UNSIGNED_INT, 0);
    }

    private static float xSpeed = (float) 0.005;
    private static float ySpeed = (float) 0.005;

    public void update(long window) {
        matrix = matrix.translate(xSpeed, ySpeed, 0);
        x += xSpeed;
        y += ySpeed;

        float bruhX = x;
        float bruhY = y;

        if (x >= 0.75 || x <= -1) { // PLUSX = RIGHT / MINUSX = LEFT
            if(x <= 0) {
                System.out.println("X/LEFT: "+Float.toString(bruhX));
            }
            if(x >= 0.1) {
                System.out.println("X/RIGHT: "+Float.toString(bruhX));
            }
            xSpeed = -xSpeed;

        }
        if (y >= 1 || y <= -0.75) { // PLUSY = TOP / MINUSY = BOTTOM
            if(x <= 0) {
                System.out.println("Y/BOT: "+Float.toString(bruhY));
            }
            if(x >= 0.1) {
                System.out.println("Y/TOP: "+Float.toString(bruhY));
            }
            ySpeed = -ySpeed;
        }

        // TODO: Send to GPU only if position updated
        matrix.get(matrixFloatBuffer);
        GL33.glUniformMatrix4fv(uniformMatrixLocation, false, matrixFloatBuffer);
    }

    private static void loadImage() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer img = STBImage.stbi_load("resources/textures/img.png", w, h, comp, 3);
            if (img != null) {
                img.flip();

                GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, w.get(), h.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, img);
                GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

                STBImage.stbi_image_free(img);
            }
        }
    }

    public float getX() { return x; }

    public float getY() { return y; }

    public float getZ() { return z; }
}