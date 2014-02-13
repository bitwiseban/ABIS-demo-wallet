Note: This is the ABIS-demo-wallet fork of Multibit.
For the original Multibit project please visit: https://github.com/jim618/multibit

### MultiBit configuration options

All these options are set in the main control file `multibit.properties`. MultiBit looks in a few places for this file in the following order:

1. Current working directory when launched. This is for backwards compatibility and for running from a USB drive.

2. (Mac OS X only) Four directory levels up. This is for running from a USB drive, but outside the OSX `.app` directory.
 
3. The operating system's standard application data directory:

#### Windows

* `System.getenv("APPDATA")/MultiBit`
* Example: `C:/Documents and Settings/Administrator/Application Data/MultiBit`

#### Mac OS X

* `System.getProperty("user.home")/Library/Application Support/MultiBit`
* Example: `/Users/jim/Library/Application Support/MultiBit`

#### Linux

* `System.getProperty("user.home")/MultiBit`
* Example: `/Users/jim/MultiBit`

Wherever this file is found, that directory is used as the application data directory for Multibit.

#### Show Delete Wallet

To enable the display in the File menu of the 'Delete Wallet' option, set `showDeleteWallet=true`

When deleting a wallet, to avoid accidental deletion, MultiBit asks you:
```
"To confirm this action enter the name of the creator of Bitcoin"
```
The answer is:
```
satoshi nakamoto
```

Note that when a wallet is deleted the wallet data directory and all the wallet backups are NOT deleted.
You have to [securely delete (shred)](http://superuser.com/questions/86824/how-to-secure-delete-file-or-folder-in-windows) this directory manually (as long as you are sure you do not
need them). 

#### Connect to specific peers

If you want to connect to specific peers set `peers=<comma separated list of peers to connect to>`

The list of peers can be specified using domain names (`www.myNode.com`) or IP addresses. Example:

```
peers=173.242.119.177, 176.9.42.247, 217.79.19.226, 98.216.173.54
```

(The older property `singleNodeConnection` is still honoured but is now deprecated. Use 'peers' instead).

Note: ONLY these peers will be used for connections.

#### Testnet

To use Testnet set `testOrProductionNetwork=test`.

To use Testnet3 (recommended) set `testOrProductionNetwork=testnet3`.
