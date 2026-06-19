# Boot repo copy

This folder is a standalone copy of the Hotspot Automator Android project.

Use it when you want the same new app code separated into a new repository named `boot`:

```bash
cd boot
git init
git add .
git commit -m "Add Hotspot Automator app"
```

Then connect it to your GitHub repo URL:

```bash
git remote add origin <your-boot-repo-url>
git push -u origin main
```
