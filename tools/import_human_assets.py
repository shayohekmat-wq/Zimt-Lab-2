#!/usr/bin/env python3
"""Import genuine Kenney CC0 artist-created PNG sprites into Android resources.

Source images are real game-art thumbnails rendered from Kenney's original GLB
meshes, redistributed by Hidencod/tge-assets (see provenance document).
Do NOT pretend these previews are separated front/rear occlusion passes.
"""
from pathlib import Path
from urllib.request import urlopen
import io
from PIL import Image

BASE = "https://raw.githubusercontent.com/Hidencod/tge-assets/main/packs"
ASSETS = {
    "kenney_human_barrel": "food-kit/thumbs/barrel.webp",
    "kenney_human_pot": "food-kit/thumbs/pot.webp",
    "kenney_human_open_box": "furniture-kit/thumbs/cardboardboxopen.webp",
    "kenney_human_pirate_chest": "pirate-kit/thumbs/chest.webp",
    "kenney_human_crate": "pirate-kit/thumbs/crate.webp",
}
OUT = Path("app/src/main/res/drawable-nodpi")
OUT.mkdir(parents=True, exist_ok=True)
for name, relative in ASSETS.items():
    url = f"{BASE}/{relative}"
    with urlopen(url, timeout=45) as response:
        data = response.read()
    image = Image.open(io.BytesIO(data)).convert("RGBA")
    if image.width < 128 or image.height < 128:
        raise ValueError(f"Too small: {url} {image.size}")
    # Preserve genuine source pixels, no AI redraw or procedural replacement.
    path = OUT / f"{name}.png"
    image.save(path, optimize=True)
    print(f"{path}: {image.size}, {len(data)} source bytes, {url}")
