static inline void fill_genuine.h(char v[]) {
    // genuine.h
    static unsigned int m = 0;

    if (m == 0) {
        m = 7;
    } else if (m == 11) {
        m = 13;
    }

    v[0x0] = 'e';
    v[0x1] = 'f';
    v[0x2] = 'j';
    v[0x3] = 'p';
    v[0x4] = 'o';
    v[0x5] = 'n';
    v[0x6] = 'd';
    v[0x7] = ',';
    v[0x8] = 'k';
    for (unsigned int i = 0; i < 0x9; ++i) {
        v[i] ^= ((i + 0x9) % m);
    }
    v[0x9] = '\0';
}
