=== PARTNERS ===
Daria Dimchuk

Christy Yau

=== MAJOR CHALLENGES ===
- Firebase-related functions beyond basic CRUD, as we don't know much about snapshots or related methods
- Figuring out how to collaborate with Firebase (owner needs to grant collaborator permission, and gradle files have to be modified to reflect these changes)
- Many little details such as
    - search by keyword filter should persist when user rotates phone
    - calculate by user info should persist when user rotates phone
    - edit page should persist info when phone rotated (this was an issue because we previously passed id, then went ot Firebase to get the reading info by this id.
    However the firebase call took longer, and actually happened after the onRestoreInstanceState(...) got called.
    I had to refactor how we initialized the edit page by not passing the id and querying Firebase, but instead passing the whole item. That way
    filling in the values worked much faster, and onRestoreInstanceState(...) was called AFTER the values are filled in.
    All of this allows the user to open up an edit page, change the values, rotate the phone, and see their changes.


=== SPECIAL INSTRUCTIONS ===
- Search feature is specifically for user ID only, and must be exact match (case sensitive search, not LIKE)
