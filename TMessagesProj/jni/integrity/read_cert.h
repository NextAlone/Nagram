//
// Created by xtao on 2024/11/10.
//

#ifndef NAGRAM_READ_CERT_H
#define NAGRAM_READ_CERT_H

#include <string>
#include <sys/syscall.h>
#include <unistd.h>
#include "log.h"

#define APK_SIGNING_BLOCK_MAGIC     "APK Sig Block 42"
#define SIGNATURE_SCHEME_V2_MAGIC   0x7109871a
#define EOCD_MAGIC                  0x6054b50

struct signing_block {
    uint64_t block_sz;
    struct id_value_pair {
        uint64_t len;
        struct /* v2_signature */ {
            uint32_t id;
            uint8_t value[0];  // size = (len - 4)
        };
    } id_value_pair_sequence[0];
    uint64_t block_sz_;   // *MUST* be same as block_sz
    char magic[16];       // "APK Sig Block 42"
};

struct EOCD {
    uint32_t magic;            // 0x6054b50
    uint8_t pad[8];            // 8 bytes of irrelevant data
    uint32_t central_dir_sz;   // size of central directory
    uint32_t central_dir_off;  // offset of central directory
    uint16_t comment_sz;       // size of comment
    char comment[0];
} __attribute__((packed));

std::string read_certificate(int fd);

bool more_check(int fd, const char* path);

#endif //NAGRAM_READ_CERT_H
