# Build and install for self-hosted TFS:

Open build.sh
Change the following values

```
TFS_SERVERNAME=http://desktop-2760qqq:8080/tfs
TFS_PAT=lgnfxowe2tgr743y3w3qwhdvtcb2xxtyt6anrnkmhrsin5m3zklq

```

To match your own server.


```
./build.sh --local
```

# Build and install to marketplace

```
./build.sh
```

PAT for marketplace should be stored in ~/.tfs_pat.

# Compilation

## Compilation issues

To get rid of 'Cannot find module' errors

```
index.ts(3,23): error TS2307: Cannot find module 'http'.
index.ts(4,24): error TS2307: Cannot find module 'https'.
index.ts(6,21): error TS2307: Cannot find module 'fs'.
index.ts(72,13): error TS2304: Cannot find name 'process'.
index.ts(171,13): error TS2304: Cannot find name 'process'.
```

```
typings install dt~node --global --save
```
