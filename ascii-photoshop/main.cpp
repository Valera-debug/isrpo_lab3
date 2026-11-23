#include <fstream>
#include <iostream>
#include <new>

namespace {

// Изображение: height строк по width символов.
// Контейнеры стандартной библиотеки использовать нельзя,
// поэтому матрица хранится как массив указателей на массивы char.
struct Image {
    int height = 0;
    int width = 0;
    char** rows = nullptr;
};

void freeImage(Image& image) {
    if (image.rows != nullptr) {
        for (int i = 0; i < image.height; ++i) {
            delete[] image.rows[i];
        }
        delete[] image.rows;
        image.rows = nullptr;
    }
    image.height = 0;
    image.width = 0;
}

char** allocateRows(int height, int width) {
    char** rows = new char*[height];
    for (int i = 0; i < height; ++i) {
        rows[i] = new char[width];
    }
    return rows;
}

// Читает изображение из файла. Возвращает false, если файл не открылся
// или содержимое не удалось прочитать.
bool readImage(const char* path, Image& image) {
    std::ifstream input(path, std::ios::binary);
    if (!input) {
        return false;
    }

    if (!(input >> image.height >> image.width)) {
        return false;
    }
    if (image.height <= 0 || image.width <= 0) {
        return false;
    }

    image.rows = allocateRows(image.height, image.width);

    // Пробелы в изображении отсутствуют, поэтому все переводы строк
    // (в том числе \r\n) можно просто пропускать.
    for (int i = 0; i < image.height; ++i) {
        for (int j = 0; j < image.width; ++j) {
            char symbol = '\0';
            do {
                if (!input.get(symbol)) {
                    return false;
                }
            } while (symbol == '\n' || symbol == '\r');
            image.rows[i][j] = symbol;
        }
    }

    return true;
}

// Записывает изображение в файл. Возвращает false при ошибке записи.
bool writeImage(const char* path, const Image& image) {
    std::ofstream output(path, std::ios::binary | std::ios::trunc);
    if (!output) {
        return false;
    }

    output << image.height << ' ' << image.width << '\n';
    for (int i = 0; i < image.height; ++i) {
        output.write(image.rows[i], image.width);
        output.put('\n');
    }

    output.flush();
    return static_cast<bool>(output);
}

// H: отражение слева направо.
void flipHorizontally(Image& image) {
    for (int i = 0; i < image.height; ++i) {
        char* row = image.rows[i];
        for (int left = 0, right = image.width - 1; left < right; ++left, --right) {
            char tmp = row[left];
            row[left] = row[right];
            row[right] = tmp;
        }
    }
}

// V: отражение сверху вниз.
void flipVertically(Image& image) {
    for (int top = 0, bottom = image.height - 1; top < bottom; ++top, --bottom) {
        char* tmp = image.rows[top];
        image.rows[top] = image.rows[bottom];
        image.rows[bottom] = tmp;
    }
}

// R: поворот на 90 градусов по часовой стрелке.
void rotateClockwise(Image& image) {
    const int newHeight = image.width;
    const int newWidth = image.height;

    Image rotated;
    rotated.height = newHeight;
    rotated.width = newWidth;
    rotated.rows = allocateRows(newHeight, newWidth);

    for (int i = 0; i < newHeight; ++i) {
        for (int j = 0; j < newWidth; ++j) {
            rotated.rows[i][j] = image.rows[image.height - 1 - j][i];
        }
    }

    freeImage(image);
    image = rotated;
}

void applyCommand(Image& image, char command) {
    switch (command) {
        case 'H':
            flipHorizontally(image);
            break;
        case 'V':
            flipVertically(image);
            break;
        case 'R':
            rotateClockwise(image);
            break;
        default:
            break;
    }
}

void applyCommands(Image& image, const char* commands) {
    for (const char* command = commands; *command != '\0'; ++command) {
        applyCommand(image, *command);
    }
}

}  // namespace

int main(int argc, char** argv) {
    if (argc != 4) {
        std::cerr << "Usage: " << argv[0] << " IMAGE COMMANDS OUTPUT\n";
        return 1;
    }

    const char* inputPath = argv[1];
    const char* commands = argv[2];
    const char* outputPath = argv[3];

    Image image;

    try {
        if (!readImage(inputPath, image)) {
            freeImage(image);
            std::cerr << "Error: cannot read input file: " << inputPath << '\n';
            return 1;
        }

        applyCommands(image, commands);

        const bool written = writeImage(outputPath, image);
        freeImage(image);

        if (!written) {
            std::cerr << "Error: cannot write output file: " << outputPath << '\n';
            return 1;
        }
    } catch (const std::bad_alloc&) {
        freeImage(image);
        std::cerr << "Error: out of memory\n";
        return 1;
    }

    return 0;
}
