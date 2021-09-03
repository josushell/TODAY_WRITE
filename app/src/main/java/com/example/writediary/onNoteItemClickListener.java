package com.example.writediary;

import android.view.View;

public interface onNoteItemClickListener {
    public void onNoteClick(NoteAdapter.ViewHolder holder, View view, int position);
}
