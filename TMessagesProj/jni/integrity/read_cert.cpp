#include <jni.h>
#include <android/log.h>
#include <sys/syscall.h>
#include <fcntl.h>
#include <sys/stat.h>
#include "read_cert.h"

std::string read_certificate(int fd) {

    uint32_t size4;
    uint64_t size8;

    // Find EOCD
    for (int i = 0;; i++) {
        // i is the absolute offset to end of file
        uint16_t comment_sz = 0;
        lseek(fd, -((off_t) sizeof(comment_sz)) - i, SEEK_END);
        read(fd, &comment_sz, sizeof(comment_sz));
        if (comment_sz == i) {
            // Double check if we actually found the structure
            lseek(fd, -((off_t) sizeof(EOCD)), SEEK_CUR);
            uint32_t magic = 0;
            read(fd, &magic, sizeof(magic));
            if (magic == EOCD_MAGIC) {
                break;
            }
        }

        if (i == 0xffff) {
            // Comments cannot be longer than 0xffff (overflow), abort
            return {};
        }
    }

    // We are now at EOCD + sizeof(magic)
    // Seek and read central_dir_off to find start of central directory

    uint32_t central_dir_off = 0;
    {
        constexpr off_t off = offsetof(EOCD, central_dir_off) - sizeof(EOCD::magic);
        lseek(fd, off, SEEK_CUR);
    }

    read(fd, &central_dir_off, sizeof(central_dir_off));

    // Next, find the start of the APK signing block
    {
        constexpr int off = sizeof(signing_block::block_sz_) + sizeof(signing_block::magic);
        lseek(fd, (off_t) (central_dir_off - off), SEEK_SET);
    }

    read(fd, &size8, sizeof(size8));  // size8 = block_sz_
    char magic[sizeof(signing_block::magic)] = {0};
    read(fd, magic, sizeof(magic));

    if (memcmp(magic, APK_SIGNING_BLOCK_MAGIC, sizeof(magic)) != 0) {
        // Invalid signing block magic, abort
        return {};
    }

    uint64_t signing_blk_sz = 0;
    lseek(fd, (off_t) (central_dir_off - size8 - sizeof(signing_blk_sz)), SEEK_SET);
    read(fd, &signing_blk_sz, sizeof(signing_blk_sz));

    if (signing_blk_sz != size8) {
        // block_sz != block_sz_, invalid signing block format, abort
        return {};
    }
    // Finally, we are now at the beginning of the id-value pair sequence
    for (;;) {
        read(fd, &size8, sizeof(size8)); // id-value pair length
        if (size8 == signing_blk_sz) {
            // Outside of the id-value pair sequence; actually reading block_sz_
            break;
        }
        uint32_t id;
        read(fd, &id, sizeof(id));
        if (id == SIGNATURE_SCHEME_V2_MAGIC) {
            read(fd, &size4, sizeof(size4)); // signer sequence length
            read(fd, &size4, sizeof(size4)); // signer length
            read(fd, &size4, sizeof(size4)); // signed data length
            read(fd, &size4, sizeof(size4)); // digest sequence length
            lseek(fd, (off_t) (size4), SEEK_CUR); // skip all digests
            read(fd, &size4, sizeof(size4)); // cert sequence length
            read(fd, &size4, sizeof(size4)); // cert length
            std::string cert;
            cert.resize(size4);
            read(fd, (void *) cert.data(), size4);
            return cert;
        } else {
            // Skip this id-value pair
            lseek(fd, (off_t) (size8 - sizeof(id)), SEEK_CUR);
        }
    }
    return {};
}

bool more_check(int fd, const char* path) {
    // check apk path
    char buff[PATH_MAX] = {0};
    std::string fdPath("/proc/");
    fdPath.append(std::to_string(getpid())).append("/fd/").append(std::to_string(fd));
    long len = syscall(__NR_readlinkat, AT_FDCWD, fdPath.c_str(), buff, PATH_MAX);
    if (len < 0) {
        LOGE("Failed to read link for fd %d", fd);
        return false;
    }
    buff[len] = '\0';
    if (strcmp(path, buff) != 0) {
        LOGE("Path mismatch: expected '%s', got '%s'", path, buff);
        return false;
    }
    // fstat
    struct stat statBuff = {0};
    long stat = syscall(__NR_fstat, fd, &statBuff);
    if (stat < 0) {
        LOGE("Failed to fstat fd %d", fd);
        return false;
    }
    // check uid&gid
    if (statBuff.st_uid != 1000 && statBuff.st_gid != 1000) {
        LOGE("UID/GID check failed for fd %d: UID=%d, GID=%d", fd, statBuff.st_uid, statBuff.st_gid);
        return false;
    }
    return true;
}
