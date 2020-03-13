package com.thepost.app.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.dialogplus.DialogPlus;
import com.squareup.picasso.Picasso;
import com.thepost.app.R;
import com.thepost.app.models.SlcmModel.BasicModel.Attendance;
import com.thepost.app.models.SlcmModel.BasicModel.InternalMarks;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

@Keep
public class SLCMAdapter extends RecyclerView.Adapter<SLCMAdapter.ViewHolder> {

    private List<Attendance> list;
    private Context mContext;
    private List<String> pictures;
    private List<Integer> nums;
    private List<InternalMarks> marks;

    public SLCMAdapter(List<Attendance> list, List<InternalMarks> marks, Context context, List<String> pictures, List<Integer> nums) {
        this.list = list;
        mContext = context;
        this.pictures = pictures;
        this.nums = nums;
        this.marks = marks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout cl = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_slcm, parent, false);

        return new ViewHolder(cl);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final int pos = position;

        double att = Double.parseDouble(list.get(position).getClassesAttended()) / Integer.parseInt(list.get(position).getTotalClasses());
        att *= 100;

        long attendancePercent = Math.round(att);

        holder.subjectName.setText(list.get(position).getSubjectName());
        holder.totalClass.setText(list.get(position).getTotalClasses());
        holder.absentClass.setText(list.get(position).getClassesAbsent());

        holder.percentAtt.setText(Long.toString(attendancePercent));

        holder.subjectNameDetailed.setText(list.get(position).getSubjectName());
        holder.totalClassDetailed.setText(list.get(position).getTotalClasses());
        holder.absentClassDetailed.setText(list.get(position).getClassesAbsent());

        holder.percentAttDetailed.setText(Long.toString(attendancePercent));

        if (attendancePercent < 75 && !list.get(position).getTotalClasses().equals("0")) {
            holder.timeHolder.setBackground(mContext.getDrawable(R.drawable.gradient_danger));
        } else if (attendancePercent <= 85 && !list.get(position).getTotalClasses().equals("0")) {

            holder.timeHolder.setBackground(mContext.getDrawable(R.drawable.gradient_risky));
        } else {

            holder.timeHolder.setBackground(mContext.getDrawable(R.drawable.gradient_safe));
        }

        String s = pictures.get(nums.get(position));

        holder.ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        long updateAtt, updateMarks;

        try {
            updateAtt = list.get(position).getUpdatedAt();
        } catch (Exception e) {
            updateAtt = -1;
        }

        try {
            updateMarks = marks.get(position).getUpdatedAt();
        } catch (Exception e) {
            updateMarks = -1;
        }

        try {
            if (updateAtt == -1 && updateMarks == -1) {

                holder.timeHolder.findViewById(R.id.textSLCMUpdate).setVisibility(View.VISIBLE);
                holder.timeHolder.findViewById(R.id.timeSLCMUpdate).setVisibility(View.VISIBLE);
            } else {

                long finalTime = updateAtt > updateMarks ? updateAtt : updateMarks;

                PrettyTime prettyTime = new PrettyTime();
                holder.timeHolder.findViewById(R.id.textSLCMUpdate).setVisibility(View.VISIBLE);
                holder.timeHolder.findViewById(R.id.timeSLCMUpdate).setVisibility(View.VISIBLE);
                ((TextView) holder.timeHolder.findViewById(R.id.timeSLCMUpdate)).setText(prettyTime.format(new Date(finalTime)));
            }
        } catch (Exception e) {

            holder.timeHolder.findViewById(R.id.textSLCMUpdate).setVisibility(View.VISIBLE);
            holder.timeHolder.findViewById(R.id.timeSLCMUpdate).setVisibility(View.VISIBLE);
        }

        Picasso.with(mContext).load(s).into(holder.photo);

        if (!marks.get(position).getStatus()) {
            holder.detailed_core.setVisibility(View.GONE);
            holder.detailed_lab.setVisibility(View.GONE);
        } else {

            holder.labEmpty.setVisibility(View.GONE);

            if (marks.get(position).getIsLab()) {
                holder.assignmentDetailed.setVisibility(View.GONE);
                holder.sessionalDetailed.setVisibility(View.GONE);
                holder.totalMarksDetailed.setVisibility(View.GONE);

                holder.dividerCore.setVisibility(View.GONE);
                holder.dividerCore2.setVisibility(View.GONE);

                holder.detailed_lab.setVisibility(View.VISIBLE);

                final int num = marks.get(position).getLab().getAssessments().size();

                Log.e(list.get(position).getSubjectName(), "" + num);

                if (num == 0) {
                    holder.labEmpty.setVisibility(View.VISIBLE);
                    holder.labViewMore.setVisibility(View.GONE);
                } else {

                    holder.labEmpty.setVisibility(View.GONE);
                    holder.labViewMore.setVisibility(View.VISIBLE);

                    for (int i = 0; i < ((num > 4) ? 4 : num); i++) {

                        View labView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.rv_lab, holder.detailed_lab, false);
                        ((TextView) labView.findViewById(R.id.labDesc)).setText(marks.get(position).getLab().getAssessments().get(i).getAssessmentDesc());
                        ((TextView) labView.findViewById(R.id.labMarks)).setText(marks.get(position).getLab().getAssessments().get(i).getMarks());
                        holder.detailed_lab.addView(labView);
                    }

                    holder.labViewMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final DialogPlus dialogPlus = DialogPlus.newDialog(mContext)
                                    .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.dialog_empty))
                                    .setCancelable(true)
                                    .setExpanded(false)
                                    .create();

                            LinearLayout view1 = (LinearLayout) dialogPlus.getHolderView();

                            ((TextView) view1.findViewById(R.id.subjectNameLab)).setText(holder.subjectName.getText());

                            for (int i = 0; i < num; i++) {

                                View labView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.rv_lab, holder.detailed_lab, false);
                                ((TextView) labView.findViewById(R.id.labDesc)).setText(marks.get(pos).getLab().getAssessments().get(i).getAssessmentDesc());
                                ((TextView) labView.findViewById(R.id.labMarks)).setText(marks.get(pos).getLab().getAssessments().get(i).getMarks());
                                view1.addView(labView);
                            }

                            dialogPlus.show();
                        }
                    });

                }

            } else {

                holder.assignment1.setText(marks.get(position).getAssignment().getOne().equals("NA") ? "-" : marks.get(position).getAssignment().getOne());
                holder.assignment2.setText(marks.get(position).getAssignment().getTwo().equals("NA") ? "-" : marks.get(position).getAssignment().getTwo());
                holder.assignment3.setText(marks.get(position).getAssignment().getThree().equals("NA") ? "-" : marks.get(position).getAssignment().getThree());
                holder.assignment4.setText(marks.get(position).getAssignment().getFour().equals("NA") ? "-" : marks.get(position).getAssignment().getFour());

                holder.sessional1.setText(marks.get(position).getSessional().getOne().equals("NA") ? "-" : marks.get(position).getSessional().getOne());
                holder.sessional2.setText(marks.get(position).getSessional().getTwo().equals("NA") ? "-" : marks.get(position).getSessional().getTwo());

                if(holder.assignment1.getText().toString().equals("-") &&
                        holder.assignment2.getText().toString().equals("-") &&
                        holder.assignment3.getText().toString().equals("-") &&
                        holder.assignment4.getText().toString().equals("-") &&
                        holder.sessional1.getText().toString().equals("-") &&
                        holder.sessional2.getText().toString().equals("-")){
                    holder.totalMarks.setText("-");
                }
                else{

                    float marks = Float.parseFloat(holder.assignment1.getText().toString().equals("-") ? "0" : holder.assignment1.getText().toString()) +
                            Float.parseFloat(holder.assignment2.getText().toString().equals("-") ? "0" : holder.assignment2.getText().toString()) +
                            Float.parseFloat(holder.assignment3.getText().toString().equals("-") ? "0" : holder.assignment3.getText().toString()) +
                            Float.parseFloat(holder.assignment4.getText().toString().equals("-") ? "0" : holder.assignment4.getText().toString()) +
                            Float.parseFloat(holder.sessional1.getText().toString().equals("-") ? "0" : holder.sessional1.getText().toString()) +
                            Float.parseFloat(holder.sessional2.getText().toString().equals("-") ? "0" : holder.sessional2.getText().toString());

                    int marks_final = (int) Math.ceil(marks);

                    float total = Float.parseFloat(holder.assignment1.getText().toString().equals("-") ? "0" : "5") +
                            Float.parseFloat(holder.assignment2.getText().toString().equals("-") ? "0" : "5") +
                            Float.parseFloat(holder.assignment3.getText().toString().equals("-") ? "0" : "5") +
                            Float.parseFloat(holder.assignment4.getText().toString().equals("-") ? "0" : "5") +
                            Float.parseFloat(holder.sessional1.getText().toString().equals("-") ? "0" : "15") +
                            Float.parseFloat(holder.sessional2.getText().toString().equals("-") ? "0" : "15");

                    int total_final = (int) total;

                    holder.totalMarks.setText(Integer.toString(marks_final));
                    holder.totalMarksFull.setText(Integer.toString(total_final));
                }
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if(marks.get(position).getStatus()) {

                    if (view.findViewById(R.id.cover_slcm).getVisibility() == View.VISIBLE) {

                        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), (int) view.getResources().getDimension(R.dimen.detailed_card_vew_height_real));
                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                                layoutParams.height = val;

                                view.setLayoutParams(layoutParams);
                            }
                        });

                        anim.setDuration(500);
                        anim.start();

                        view.findViewById(R.id.cover_slcm).setVisibility(View.GONE);
                        view.findViewById(R.id.detailed_slcm).setVisibility(View.VISIBLE);
                    } else {

                        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), (int) view.getResources().getDimension(R.dimen.card_vew_height_real));
                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                                layoutParams.height = val;
                                view.setLayoutParams(layoutParams);
                            }
                        });

                        anim.setDuration(500);
                        anim.start();

                        view.findViewById(R.id.detailed_slcm).setVisibility(View.GONE);
                        view.findViewById(R.id.cover_slcm).setVisibility(View.VISIBLE);
                    }
                }
                else{

                    final DialogPlus dialogPlus = DialogPlus.newDialog(mContext)
                            .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.dialog_empty))
                            .setCancelable(true)
                            .setExpanded(false)
                            .create();

                    LinearLayout view1 = (LinearLayout) dialogPlus.getHolderView();

                    view1.findViewById(R.id.subjectNameLab).setVisibility(View.VISIBLE);
                    view1.findViewById(R.id.extraInfo).setVisibility(View.VISIBLE);

                    ((TextView) view1.findViewById(R.id.subjectNameLab)).setText(marks.get(position).getSubjectName());
                    ((TextView) view1.findViewById(R.id.extraInfo)).setText("There's no marks to display for this subject right now. Check back later!");

                    dialogPlus.show();
                }
            }
        });


        holder.timeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DialogPlus dialogPlus = DialogPlus.newDialog(mContext)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.dialog_empty))
                        .setCancelable(true)
                        .setExpanded(false)
                        .create();

                LinearLayout view1 = (LinearLayout) dialogPlus.getHolderView();

                view1.findViewById(R.id.subjectNameLab).setVisibility(View.GONE);
                view1.findViewById(R.id.extraInfo).setVisibility(View.VISIBLE);

                if (!((TextView) holder.timeHolder.findViewById(R.id.timeSLCMUpdate)).getText().toString().trim().equals("--"))
                    ((TextView) view1.findViewById(R.id.extraInfo)).setText("This data (attendance and marks) was last updated in SLCM " + ((TextView) holder.timeHolder.findViewById(R.id.timeSLCMUpdate)).getText().toString());
                else
                    ((TextView) view1.findViewById(R.id.extraInfo)).setText("This data (attendance and marks) hasn't been updated in SLCM since your first login.");

                dialogPlus.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectName;
        TextView subjectNameDetailed;
        TextView totalClass;
        TextView absentClass;
        TextView percentAtt;
        TextView totalClassDetailed;
        TextView absentClassDetailed;
        TextView percentAttDetailed;
        TextView assignment1;
        TextView assignment2;
        TextView assignment3;
        TextView assignment4;
        TextView sessional1;
        TextView sessional2;
        TextView labEmpty;
        ImageView photo;
        LinearLayout ll;
        LinearLayout timeHolder;
        LinearLayout detailed_core;
        LinearLayout assignmentDetailed;
        LinearLayout sessionalDetailed;
        LinearLayout totalMarksDetailed;
        LinearLayout detailed_lab;
        View dividerCore;
        View dividerCore2;
        FrameLayout photoFrame;
        TextView totalMarks;
        TextView totalMarksFull;

        TextView labViewMore;

        ViewHolder(LinearLayout ll) {

            super(ll);

            this.ll = ll;

            subjectName = ll.findViewById(R.id.subjectName);
            subjectNameDetailed = ll.findViewById(R.id.subjectNameDetailed);
            totalClass = ll.findViewById(R.id.totalAttendance);
            absentClass = ll.findViewById(R.id.totalAbsent);
            percentAtt = ll.findViewById(R.id.totalPercent);

            totalClassDetailed = ll.findViewById(R.id.totalAttendanceDetailed);
            absentClassDetailed = ll.findViewById(R.id.totalAbsentDetailed);
            percentAttDetailed = ll.findViewById(R.id.totalPercentDetailed);

            assignment1 = ll.findViewById(R.id.assignment1);
            assignment2 = ll.findViewById(R.id.assignment2);
            assignment3 = ll.findViewById(R.id.assignment3);
            assignment4 = ll.findViewById(R.id.assignment4);

            sessional1 = ll.findViewById(R.id.sessional1);
            sessional2 = ll.findViewById(R.id.sessional2);

            photo = ll.findViewById(R.id.slcmPhoto);
            labEmpty = ll.findViewById(R.id.show_empty_lab);

            timeHolder = ll.findViewById(R.id.timeShowSLCM);
            detailed_core = ll.findViewById(R.id.detailed_core);
            detailed_lab = ll.findViewById(R.id.detailed_lab);

            assignmentDetailed = ll.findViewById(R.id.assignmentDetailed);
            sessionalDetailed = ll.findViewById(R.id.sessionalDetailed);
            totalMarksDetailed = ll.findViewById(R.id.totalMarksDetailed);

            dividerCore = ll.findViewById(R.id.dividerCore);
            dividerCore2 = ll.findViewById(R.id.dividerCore2);
            photoFrame = ll.findViewById(R.id.slcmPhotoFrame);

            labViewMore = ll.findViewById(R.id.show_more_lab);
            totalMarks = ll.findViewById(R.id.total_marks);
            totalMarksFull = ll.findViewById(R.id.total_marks_full);
        }
    }
}
